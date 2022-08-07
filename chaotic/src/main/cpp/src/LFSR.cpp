//
// Created by nguye on 8/8/2022.
//

#include "LFSR.h"

LFSR::LFSR(byte seed, int iterations) {
    for (auto i = 0; i < iterations; ++i) {
        seed = seed == 0 ? 28 : seed;
        byte lfsr = seed;
        // Taps: 8, 6, 5, 3; feedback polynomial: x^8 + x^6 + x^5 + x^3 + 1
        byte bit = ((lfsr >> 0) ^ (lfsr >> 2) ^ (lfsr >> 3) ^ (lfsr >> 5)) & 1;
        lfsr = (lfsr >> 1) | (bit << 7);
        data.push_back(lfsr);
        seed = lfsr;
    }
}

byte LFSR::operator[](int i) {
    return data[i];
}

double LFSR::next() {
    bytes lfsr = bytes(data.begin()+currentIndex, data.begin()+currentIndex+8);
    currentIndex += 8;
    if (currentIndex >= data.size()) {
        currentIndex = 0;
    }
    return convertByteArrayToDouble(lfsr);
}

