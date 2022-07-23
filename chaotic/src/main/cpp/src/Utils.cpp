#include "Utils.h"

#include <sstream>
#include <iomanip>

std::string byte2hex(const bytes& digest) {
    std::stringstream ss;
    ss << std::hex << std::setfill('0');
    for (unsigned char i : digest) {
        ss << std::setw(2) << (int) i;
    }
    return ss.str();
}

bytes hex2byte(const std::string& hexStr) {
    auto digest = bytes(hexStr.length() / 2);
    for (size_t i = 0; i < hexStr.size(); i += 2) {
        std::string byteString = hexStr.substr(i, 2);
        digest[i / 2] = (byte) strtol(byteString.c_str(), nullptr, 16);
    }
    return digest;
}

std::string bytes2string(const bytes& data) {
    std::string res;
    std::for_each(data.begin(), data.begin() + data.size(), [&](byte b) {
        res += static_cast<char>(b);
    });
    return res;
}

bytes string2bytes(const std::string& data) {
    auto res = bytes(data.length());
    std::for_each(data.begin(), data.end(), [&](char c) {
        res[std::distance(data.begin(), std::find(data.begin(), data.end(), c))] = static_cast<byte>(c);
    });
    return res;
}
