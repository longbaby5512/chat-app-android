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
    int iter = 0;
    for (auto i = 0; i < digest.size(); i += 2) {
        x = x ^ digest[i];
        m = m ^ digest[i + 1];
        iter += digest[i] + digest[i + 1];
    }

    double x_ = x / 255.0;
    double m_ = m / 255.0;

    double r = 3.7 + x / 255.0 * m / 255.0 * 0.3;

    if (x_ <= 0 || x_ >= 1)
        x_ = 0.5;

    if (m_ < 0 || m_ > 1)
        m_ = 0.5;

    if (r < 3.7 || r > 4)
        r = 3.99999;

    for (auto i = 0; i < iter + x + m; ++i) {
        x_ = r * x_ * (1 - x_);
        m_ = r * m_ * (1 - m_);
    }

    key.push_back(x_);
    key.push_back(3.7 + m_ * 0.3);
    if (key[0] <= 0 || key[0] >= 1) {
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
    int iter = 0;
    for (auto i = 0; i < digest.size(); i += 2) {
        x = x ^ digest[i];
        m = m ^ digest[i + 1];
        iter += digest[i] + digest[i + 1];
    }

    double x_ = x / 255.0;
    double m_ = m / 255.0;

    double r = x / 255.0 * m / 255.0;

    if (x_ <= 0 || x_ >= 1)
        x_ = 0.5;

    if (m_ < 0 || m_ > 1)
        m_ = 0.5;

    if (r < 0 || r > 1)
        r = 0.5;

    for (auto i = 0; i < iter + x + m; ++i) {
        x_ =  r * sin(M_PI * x_);
        m_ = r * sin(M_PI * m_);
    }

    key.push_back(x_);
    key.push_back(m_);
    if (key[0] <= 0 || key[0] >= 1) {
        key[0] = 0.5;
    }
    if (key[1] < 0 || key[1] > 1) {
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
