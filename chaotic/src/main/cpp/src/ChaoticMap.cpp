#include "ChaoticMap.h"
#include "LFSR.h"


#include <cmath>

doubles Logistic::sequence(const doubles &key, size_t iter) {
    auto x = key[0];
    outOfRange(x, 0, 1, 0.5);
    auto m = 3.99999;
    if (key.size() == 2) {
        m = key[1];
    }
    outOfRange(m, 3.7, 4, 3.99999);

    doubles result;
    for (auto i = 0; i < iter; ++i) {
        x = m * x * (1 - x);
        result.push_back(x);
    }

    return result;
}

doubles Logistic::key(const bytes &digest) {
    assert(digest.size() == 32);
    int x = digest[0];
    int m = digest[1];
    int iter = digest[0] + digest[1];
    byte seed = digest[0] ^ digest[1];
    for (auto i = 2; i < digest.size(); i += 2) {
        x = x + digest[i];
        m = m + digest[i + 1];
        iter += digest[i] + digest[i + 1];
        seed ^= digest[i] ^ digest[i + 1];
    }

    double x_ = x / 4096.0;
    double m_ = m / 4096.0;
    auto lfsr = LFSR(seed, iter * 8);
    for (auto i = 0; i < iter; ++i) {
        auto l = lfsr.next();
        x_ = xorDouble(x_, l);
        m_ = xorDouble(m_, x_);
        std::swap(x_, m_);
    }
    auto *x_ull = reinterpret_cast<unsigned long long *>(&x_);
    auto *m_ull = reinterpret_cast<unsigned long long *>(&m_);
    auto x_temp = *x_ull;
    auto m_temp = *m_ull;
    x_ = (double) x_temp / (double) ULLONG_MAX;
    m_ = (double) m_temp / (double) ULLONG_MAX;

    convertBetween(m_, 0.0, 1.0, 3.7, 4.0);

    for (auto i = 0; i < iter; ++i) {
        x_ = m_ * x_ * (1 - x_);
        std::swap(x_, m_);
        convertBetween(x_, 3.7, 4.0, 0.0, 1.0);
        convertBetween(m_, 0.0, 1.0, 3.7, 4.0);
    }

    return {x_, m_};
}

std::string Logistic::name() {
    return "Logistic";
}

doubles Sin::sequence(const doubles &key, size_t iter) {
    auto x = key[0];
    outOfRange(x, 0, 1, 0.5);
    auto m = 0.5;
    if (key.size() == 2) {
        m = key[1];
    }
    outOfRange(m, 0, 1, 0.5);

    doubles result;
    for (auto i = 0; i < iter; ++i) {
        x = m * sin(M_PI * x);
        result.push_back(x);
    }

    return result;
}

doubles Sin::key(const bytes &digest) {
    assert(digest.size() == 32);
    int x = digest[0];
    int m = digest[1];
    int iter = digest[0] + digest[1];
    byte seed = digest[0] ^ digest[1];
    for (auto i = 2; i < digest.size(); i += 2) {
        x = x + digest[i];
        m = m + digest[i + 1];
        iter += digest[i] + digest[i + 1];
        seed ^= digest[i] ^ digest[i + 1];
    }
    double x_ = x / 4096.0;
    double m_ = m / 4096.0;
    auto lfsr = LFSR(seed, iter * 8);
    for (auto i = 0; i < iter; ++i) {
        auto l = lfsr.next();
        x_ = xorDouble(x_, l);
        m_ = xorDouble(m_, x_);
        std::swap(x_, m_);
    }
    auto *x_ull = reinterpret_cast<unsigned long long *>(&x_);
    auto *m_ull = reinterpret_cast<unsigned long long *>(&m_);
    auto x_temp = *x_ull;
    auto m_temp = *m_ull;
    x_ = (double) x_temp / (double) ULLONG_MAX;
    m_ = (double) m_temp / (double) ULLONG_MAX;
    for (auto i = 0; i < iter; ++i) {
        x_ = m_ * sin(M_PI * x_);
        std::swap(x_, m_);
    }
    return {x_, m_};
}

std::string Sin::name() {
    return "Sin";
}

std::unique_ptr<ChaoticMap> ChaoticMap::create(int type) {
    switch (type) {
        case 0:
            return std::make_unique<Logistic>();
        case 1:
            return std::make_unique<Sin>();
        default:
            throw std::runtime_error("type must be 0 or 1");
    }
}

void ChaoticMap::outOfRange(double &x, double min, double max, double defaultValue) {
    if (x < min || x > max) {
        x = defaultValue;
    }
}
