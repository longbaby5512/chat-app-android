#include "ChaoticMap.h"

#include <cmath>

doubles Logistic::sequence(const doubles &key, size_t iter) {
    auto x = key[0];
    if (x <= 0 || x >= 1) {
        throw std::runtime_error("x must between 0 and 1");
    }
    auto m = 4.0;
    if (key.size() == 2) {
        m = key[1];
    }

    if (m < 3.7 || m > 4) {
        std::string err = "m is ";
        err += std::to_string(m);
        err += " must between 3.7 and 4";
        throw std::runtime_error(err);
    }

    doubles result;
    for (auto i = 0; i < iter; ++i) {
        x = m * x * (1 - x);
        result.push_back(x);
    }

    return result;
}

doubles Logistic::key(const bytes &digest) {
    doubles key;
    byte x = digest[0];
    byte m = digest[1];
    for (auto i = 0; i < digest.size(); i += 2) {
        x = x ^ digest[i];
        m = m ^ digest[i + 1];
    }

    // x between 0 and 1
    key.push_back(x / 255.0);
    key.push_back(3.7 + m / 255.0 * 0.3);
    if ( key[0] <= 0 || key[0] >= 1) {
        key[0] = 0.5;
    }
    if (key[1] < 3.7 || key[1] > 4) {
        key[1] = 4;
    }
    return key;
}

std::string Logistic::name() {
    return "Logistic";
}

doubles Sin::sequence(const doubles & key, size_t iter) {
    auto x = key[0];
    if (x <= 0 || x >= 1) {
        throw std::runtime_error("x must between 0 and 1");
    }
    auto m = 0.5;
    if (key.size() == 2) {
        m = key[1];
    }
    if (m <= 0 || m >= 1) {
        std::string err = "m is ";
        err += std::to_string(m);
        err += " must between 0 and 1";
        throw std::runtime_error(err);
    }

    doubles result;
    for (auto i = 0; i < iter; ++i) {
        x = m * sin(M_PI * x);
        result.push_back(x);
    }

    return result;
}

doubles Sin::key(const bytes & digest) {
    doubles key;
    byte x = digest[0];
    byte m = digest[1];
    for (auto i = 0; i < digest.size(); i += 2) {
        x = x ^ digest[i];
        m = m ^ digest[i + 1];
    }

    // x between 0 and 1
    key.push_back(x / 255.0);
    key.push_back(m / 255.0);
    if (key[0] <= 0 || key[0] >= 1) {
        key[0] = 0.5;
    }
    if (key[1] <= 0 || key[1] >= 1) {
        key[1] = 0.5;
    }
    return key;
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
