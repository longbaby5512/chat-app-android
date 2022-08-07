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
    double x = digest[0];
    double m = digest[1];
    int iter = digest[0] + digest[1];
    byte seed = digest[0] ^ digest[1];
    for (auto i = 2; i < digest.size(); i += 2) {
        x = (x + digest[i]) / 2;
        m = (m + digest[i + 1]) / 2;
        iter += digest[i] + digest[i + 1];
        seed ^= digest[i] ^ digest[i + 1];
    }

    double x_ = x / 255.0;
    double m_ = m / 255.0;
    convertBetween(m_, 0.0, 1.0, 3.7, 4.0);
    auto lfsr = LFSR(seed, iter * 8);
    for (auto i = 0; i < iter; ++i) {
        x_ = m_ * x_ * (1 - x_);
        auto x_byte_array = convertDoubleToByteArray(x_);
        for (auto j = 0; j < 8; ++j) {
            x_byte_array[j] ^= lfsr[i * 8 + j];
            x_ = (x_ + x_byte_array[j] / 255.0 + lfsr[i * 8 + j] / 255.0) / 3.0;
        }
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
    byte x = digest[0];
    byte m = digest[1];
    int iter = digest[0] + digest[1];
    for (auto i = 0; i < digest.size(); i += 2) {
        x = x ^ digest[i];
        m = m ^ digest[i + 1];
        iter += digest[i] + digest[i + 1];
    }

    double x_ = x / 255.0;
    double m_ = m / 255.0;
    byte seed = x ^ m;
    auto lfsr = LFSR(seed, iter * 8);
    for (auto i = 0; i < iter; ++i) {
        x_ = m_ * sin(M_PI * x_);
        auto x_byte_array = convertDoubleToByteArray(x_);
        for (auto j = 0; j < 8; ++j) {
            x_byte_array[j] ^= lfsr[i * 8 + j];
            x_ = (x_ + x_byte_array[j] / 255.0 + lfsr[i * 8 + j] / 255.0) / 3.0;
        }
        std::swap(x_, m_);
    }

    return doubles {x_, m_};
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
