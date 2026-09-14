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

namespace vermgr
{
    namespace util
    {
        inline uint64_t static_integer_mix(uint64_t x) {
            x ^= x >> 30;
            x *= 0xbf58476d1ce4e5b9ULL;
            x ^= x >> 27;
            x *= 0x94d049bb133111ebULL;
            return x ^ (x >> 31);
        }
        
        inline std::string getHash()
        {
            auto now = std::chrono::high_resolution_clock::now();
            auto nanos = std::chrono::duration_cast<std::chrono::nanoseconds>(
                now.time_since_epoch()
            ).count();

            auto tid = std::hash<std::thread::id>{}(std::this_thread::get_id());

            int stack_var = 0;
            uintptr_t mem_entropy = reinterpret_2_uintptr(&stack_var); // ili standardni reinterpret_cast

            static std::atomic<uint64_t> counter{14695981039346656037ULL};
            uint64_t seq = counter.fetch_add(0x9e3779b97f4a7c15ULL, std::memory_order_relaxed);

            uint64_t raw = static_integer_mix(nanos ^ seq ^ (tid * 0x9915059103L) ^ (uintptr_t)&stack_var);

            uint64_t z = raw + 0x9e3779b97f4a7c15ULL;
            z = (z ^ (z >> 30)) * 0xbf58476d1ce4e5b9ULL;
            z = (z ^ (z >> 27)) * 0x94d049bb133111ebULL;
            uint64_t h = z ^ (z >> 31);

            return std::to_string(h);
        }
    }
}

int main()
{
    copyTemplates(EXCLUDED_DIR, ROOT_DIR);

    static const std::string searchStr = "!\!NEOFORGE_MOD_VERSION!!";
    static std::string v = vermgr::util::getHash();
    std::cout << "Version generated: " << v << std::endl;

    processDirectory(ROOT_DIR, searchStr, v);

    std::cout << "Version manager finished." << std::endl;
    return 0;
}