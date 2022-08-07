#ifndef CHAT_APP_CHAOTICMAP_H
#define CHAT_APP_CHAOTICMAP_H

#include "Utils.h"


class ChaoticMap {
public:
    virtual doubles sequence(const doubles &, size_t) = 0;

    virtual doubles key(const bytes &) = 0;

    virtual std::string name() = 0;

    virtual ~ChaoticMap() = default;

    static std::unique_ptr<ChaoticMap> create(int);

protected:
    static void outOfRange(double &, double, double, double);
};

class Logistic : public ChaoticMap {
public:
    doubles sequence(const doubles &, size_t) override;

    doubles key(const bytes &) override;

    std::string name() override;

    ~Logistic() override = default;
};

class Sin : public ChaoticMap {
public:
    doubles sequence(const doubles &, size_t) override;

    doubles key(const bytes &) override;

    std::string name() override;

    ~Sin() override = default;

};

#endif //CHAT_APP_CHAOTICMAP_H
