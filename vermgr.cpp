#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <filesystem>
#include <chrono>

#define ROOT_DIR "."
#define EXCLUDED_DIR "config_template"

namespace fs = std::filesystem;

void copyTemplates(const fs::path& src, const fs::path& dest)
{
    std::cout << "Copying templates..." << std::endl;
    try
    {
        if(!fs::exists(src)) return;
        for(const auto& entry : fs::recursive_directory_iterator(src))
        {
            const auto& path = entry.path();
            auto relativePath = fs::relative(path, src);
            auto targetPath = dest / relativePath;

            if(fs::is_directory(entry.status()))
            {
                fs::create_directories(targetPath);
            }
            else if(fs::is_regular_file(entry.status()))
            {
                fs::create_directories(targetPath.parent_path());
                fs::copy_file(path, targetPath, fs::copy_options::overwrite_existing);
            }
        }
    }
    catch (const fs::filesystem_error& e)
    {
        std::cerr << "Filesystem err: " << e.what() << '\n';
    }
}

void replaceInFile(const fs::path& filePath, const std::string& target, const std::string& replacement)
{
    std::ifstream inFile(filePath);
    if(!inFile.is_open())
    {
        std::cerr << "Err: " << filePath << '\n';
        return;
    }

    std::stringstream buffer;
    buffer << inFile.rdbuf();
    std::string content = buffer.str();
    inFile.close();

    size_t pos = 0;
    bool modified = false;

    while((pos = content.find(target, pos)) != std::string::npos)
    {
        content.replace(pos, target.length(), replacement);
        pos += replacement.length();
        modified = true;
    }

    if(modified)
    {
        std::ofstream outFile(filePath, std::ios::trunc);
        if(!outFile.is_open())
        {
            std::cerr << "Err: " << filePath << '\n';
            return;
        }
        outFile << content;
        outFile.close();

        std::cout << "Modified file: " << filePath << '\n';
    }
}

void processDirectory(const std::string& dirPath, const std::string& target, const std::string& replacement)
{
    try
    {
        for(const auto& entry : fs::recursive_directory_iterator(dirPath))
        {
            if(fs::is_regular_file(entry.status()))
            {
                fs::path p = entry.path();
                bool excluded = false;
                for(auto const& part : p)
                {
                    if(part == EXCLUDED_DIR)
                    {
                        excluded = true;
                        break;
                    }
                }
                if (!excluded)
                {
                    replaceInFile(p, target, replacement);
                }
            }
        }
    }
    catch(const fs::filesystem_error& e)
    {
        std::cerr << "Filesystem err: " << e.what() << '\n';
    }
}

std::string getHash()
{
    auto now = std::chrono::high_resolution_clock::now();
    auto nanos = std::chrono::duration_cast<std::chrono::nanoseconds>(
        now.time_since_epoch()
    ).count();

    std::size_t h = std::hash<long long>{}(nanos);
    return std::to_string(h);
}

int main()
{
    copyTemplates(EXCLUDED_DIR, ROOT_DIR);

    static const std::string searchStr = "1789294273453441900";
    static std::string v = getHash();
    std::cout << "Version generated: " << v << std::endl;

    processDirectory(ROOT_DIR, searchStr, v);

    std::cout << "Version manager finished." << std::endl;
    return 0;
}