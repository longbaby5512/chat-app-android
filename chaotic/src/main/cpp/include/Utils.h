#ifndef CHAT_APP_UTILS_H
#define CHAT_APP_UTILS_H

#include <iostream>
#include <vector>
#include <algorithm>
#include <numeric>
#include <execution>
#include <algorithm>
#include <cassert>

#include <android/log.h>
#include <sstream>

#define TAG "NativeChaoticCypher"

#define LOGE(TAG,...) __android_log_print(ANDROID_LOG_ERROR,    TAG, __VA_ARGS__)
#define LOGW(TAG, ...) __android_log_print(ANDROID_LOG_WARN,     TAG, __VA_ARGS__)
#define LOGI(TAG, ...) __android_log_print(ANDROID_LOG_INFO,     TAG, __VA_ARGS__)
#define LOGD(TAG, ...) __android_log_print(ANDROID_LOG_DEBUG,    TAG, __VA_ARGS__)

typedef unsigned char byte;
typedef std::vector<byte> bytes;
typedef std::vector<double> doubles;

template <typename T>
std::ostream &operator<<(std::ostream &os, const std::vector<T> &v) {
    os << "[";
    for (auto i = 0; i < v.size(); ++i) {
        os << v[i];
        if (i != v.size() - 1) {
            os << ", ";
        }
    }
    os << "]";
    return os;
}


template <typename T>
std::string to_string(std::vector<T> v) {
    std::stringstream ss;
    ss << v;
    return ss.str();
}

std::vector<byte> convertDoubleToByteArray(double);
double convertByteArrayToDouble(const std::vector<byte> &);

template<typename ForwardIterator, typename Compare=std::less<typename std::iterator_traits<ForwardIterator>::value_type>>
std::vector<size_t> argsort(ForwardIterator first, ForwardIterator last, Compare comp = Compare()) {
    using value_type = typename std::iterator_traits<ForwardIterator>::value_type;
    using difference_type = typename std::iterator_traits<ForwardIterator>::difference_type;
    difference_type dist = std::distance(first, last);
    std::vector<size_t> indices(dist);
    std::iota(indices.begin(), indices.end(), 0);
    std::sort(indices.begin(), indices.end(), [&first, &comp](size_t left, size_t right) { return comp(*std::next(first, left), *std::next(first, right)); });
    return indices;
}

template<typename Base, typename T>
inline bool instanceof(std::unique_ptr<T> &ptr) {
    return dynamic_cast<Base *>(ptr.get()) != nullptr;
}

template<typename T>
void convertBetween(T &value, const T& minSrc, const T& maxSrc, const T &minDes, const T &maxDes) {
    value =  minDes + std::fmod(value - minSrc, maxSrc - minSrc) * (maxDes - minDes) / (maxSrc - minSrc);
}

#endif //CHAT_APP_UTILS_H
