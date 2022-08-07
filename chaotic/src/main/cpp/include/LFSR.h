//
// Created by nguye on 8/8/2022.
//

#ifndef CHAT_APP_LFSR_H
#define CHAT_APP_LFSR_H

#include "Utils.h"

class LFSR {
private:
    bytes data;
    int currentIndex{};
public:
    explicit LFSR(byte, int);

    byte operator[](int);
    double next();
};

#endif //CHAT_APP_LFSR_H
