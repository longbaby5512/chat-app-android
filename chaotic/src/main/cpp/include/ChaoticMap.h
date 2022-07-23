#ifndef CHAT_APP_CHAOTICMAP_H
#define CHAT_APP_CHAOTICMAP_H

#include "Utils.h"

class Logistic;
class Sin;

class ChaoticMap {
public:
    virtual doubles sequence (const doubles&, size_t) = 0;
    virtual doubles key(const bytes&) = 0;
    virtual std::string name() = 0;

    static std::unique_ptr<ChaoticMap> create(int);
};

class Logistic: public ChaoticMap {
public:
    doubles sequence(const doubles &, size_t) override;

    doubles key(const bytes &) override;

    std::string name() override;


};

class Sin: public ChaoticMap {
public:
    doubles sequence(const doubles &, size_t) override;

    doubles key(const bytes &) override;

    std::string name() override;

};

#endif //CHAT_APP_CHAOTICMAP_H
