#ifndef CHAT_APP_CHAOTICCYPHER_H
#define CHAT_APP_CHAOTICCYPHER_H

#include <memory>

#include "Utils.h"
#include "ChaoticMap.h"

class ChaoticCypher {
public:
    static constexpr int ENCRYPT_MODE = 0;
    static constexpr int DECRYPT_MODE = 1;

    explicit ChaoticCypher(
            std::unique_ptr<ChaoticMap> = std::make_unique<Logistic>(),
            std::unique_ptr<ChaoticMap> = std::make_unique<Logistic>(),
            std::unique_ptr<ChaoticMap> = std::make_unique<Logistic>()
    );

    void init(int, const bytes &);
    bytes doFinal(bytes &);


    friend std::ostream &operator<<(std::ostream &, const ChaoticCypher &);

    std::string info();

private:
    const size_t IGNORE_ELEMENTS = 1000;
    const size_t ITER_GEN_SBOX_DEFAULT = 1000;
    const size_t SBOX_SIZE = 256;

    std::unique_ptr<ChaoticMap> permMap;
    std::unique_ptr<ChaoticMap> subMap;
    std::unique_ptr<ChaoticMap> diffMap;
    doubles keyPerm, keySub, keyDiff;
    int mode{};
    bytes data;

    void setMode(int);
    void setKey(bytes key);

    bytes generateSBox();
    void permutation();
    void substitution();
    void diffusion();
    bytes encrypt();
    bytes decrypt();
};

#endif //CHAT_APP_CHAOTICCYPHER_H
