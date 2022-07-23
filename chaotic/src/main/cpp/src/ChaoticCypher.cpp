#include "ChaoticCypher.h"
#include "Hash.h"
#include <memory>


ChaoticCypher::ChaoticCypher(std::unique_ptr<ChaoticMap> permMap, std::unique_ptr<ChaoticMap> subMap,
                             std::unique_ptr<ChaoticMap> diffMap) {
    this->permMap = std::move(permMap);
    this->subMap = std::move(subMap);
    this->diffMap = std::move(diffMap);
}

ChaoticCypher::ChaoticCypher(ChaoticMap *permMap, ChaoticMap *subMap, ChaoticMap *diffMap) {
    this->permMap = std::unique_ptr<ChaoticMap>(permMap);
    this->subMap = std::unique_ptr<ChaoticMap>(subMap);
    this->diffMap = std::unique_ptr<ChaoticMap>(diffMap);
}

void ChaoticCypher::permutation() {
    auto len = data.size();
    auto chaoticSequence = permMap->sequence(keyPerm, len + IGNORE_ELEMENTS);
    chaoticSequence.erase(chaoticSequence.begin(), chaoticSequence.begin() + IGNORE_ELEMENTS);
    assert(chaoticSequence.size() == len);
    auto indices = argsort(chaoticSequence.begin(), chaoticSequence.end());
    bytes result(len);
    if (mode == ENCRYPT_MODE) {
        for (auto i = 0; i < len; ++i) {
            result[indices[i]] = data[i];
        }
    } else {
        for (auto i = 0; i < len; ++i) {
            result[i] = data[indices[i]];
        }
    }
    data = std::move(result);
}

bytes ChaoticCypher::generateSBox() {
    auto iter = data.size() > ITER_GEN_SBOX_DEFAULT ? data.size() : ITER_GEN_SBOX_DEFAULT;
    auto chaoticSequence = subMap->sequence(keySub, iter + SBOX_SIZE);
    chaoticSequence.erase(chaoticSequence.begin(), chaoticSequence.begin() + iter);
    assert(chaoticSequence.size() == SBOX_SIZE);
    auto indices = argsort(chaoticSequence.begin(), chaoticSequence.end());
    if (mode == ENCRYPT_MODE)
        return std::vector<byte>{indices.begin(), indices.end()};

    bytes invSbox(SBOX_SIZE);
    size_t index = 0;
    std::for_each(indices.begin(), indices.end(), [&](byte element) {
        invSbox[element] = index++;
    });
    return invSbox;
}

void ChaoticCypher::substitution() {
    auto sbox = generateSBox();
    auto len = data.size();
    bytes result(len);
    size_t index = 0;
    std::for_each(result.begin(), result.end(), [&](byte& element) {
        element = sbox[data[index++]];
    });
    data = std::move(result);
}

void ChaoticCypher::diffusion() {
    auto len = data.size();
    auto chaoticSequence = diffMap->sequence(keyDiff, len + IGNORE_ELEMENTS);
    chaoticSequence.erase(chaoticSequence.begin(), chaoticSequence.begin() + IGNORE_ELEMENTS);
    assert(chaoticSequence.size() == len);
    auto minMax = std::minmax_element(chaoticSequence.begin(), chaoticSequence.end());
    auto min = *minMax.first;
    auto max = *minMax.second;

    if (mode == ENCRYPT_MODE) {
        data[0] = data[0] ^ static_cast<byte>(254 * (chaoticSequence[0] - min) / (max - min));
        auto last = data[0];
        size_t index = 1;
        std::for_each(data.begin() + 1, data.end(), [&](byte& element) {
            element = element ^ last ^ static_cast<byte>(254 * (chaoticSequence[index] - min) / (max - min));
            last = element;
        });
    } else {
        auto last = data[0];
        data[0] = data[0] ^ static_cast<byte>(254 * (chaoticSequence[0] - min) / (max - min));
        size_t index = 1;
        std::for_each(data.begin() + 1, data.end(), [&](byte& element) {
            auto tmp = element;
            element = element ^last^ static_cast<byte>(254 * (chaoticSequence[index] - min) / (max - min));
            last = tmp;
        });
    }
}

bytes ChaoticCypher::encrypt() {
    permutation();
    substitution();
    diffusion();
    return data;
}

bytes ChaoticCypher::decrypt() {
    diffusion();
    substitution();
    permutation();
    return data;
}

bytes ChaoticCypher::doFinal(bytes &data) {
    this->data = std::move(data);
    switch (mode) {
        case ENCRYPT_MODE:
            return encrypt();
        case DECRYPT_MODE:
            return decrypt();
        default:
            throw std::runtime_error("Unknown mode");
    }
}

bytes ChaoticCypher::doFinal(std::string &data) {
    this->data = bytes(data.begin(), data.end());
    switch (mode) {
        case ENCRYPT_MODE:
            return encrypt();
        case DECRYPT_MODE:
            return decrypt();
        default:
            throw std::runtime_error("Unknown mode");
    }
}

void ChaoticCypher::init(int mode, const bytes &key) {
    setMode(mode);
    std::unique_ptr<Hash> hash = std::make_unique<SHA256>();
    hash->update(key);
    setKey(hash->digest());

}

void ChaoticCypher::setMode(int mode) {
    this->mode = mode;
}

void ChaoticCypher::setKey(bytes hash) {
    keyPerm = permMap->key(bytes(hash.begin(), hash.begin() + hash.size() / 2));
    keySub = subMap->key(bytes(hash.begin() + hash.size() / 2, hash.end()));
    keyDiff = diffMap->key(hash);
}

void ChaoticCypher::init(int mode, const std::string & key) {
    init(mode, bytes(key.begin(), key.end()));
}

std::ostream &operator<<(std::ostream & os, const ChaoticCypher &cypher) {
    std:: string mode = cypher.mode == ChaoticCypher::ENCRYPT_MODE ? "ENCRYPT" : "DECRYPT";
    std::string algPerm = cypher.permMap->name();
    std::string algSub = cypher.subMap->name();
    std::string algDiff = cypher.diffMap->name();
    os << "ChaoticCypher{mode:" << mode << ", key:{" << algPerm << cypher.keyPerm << ", " << algSub << cypher.keySub << ", " << algDiff << cypher.keyDiff << "}";
    return os;
}

std::string ChaoticCypher::info() {
    std::string modeS = this->mode == ENCRYPT_MODE ? "ENCRYPT" : "DECRYPT";
    std::string algPerm = permMap->name();
    std::string algSub = subMap->name();
    std::string algDiff = diffMap->name();
    return "ChaoticCypher{mode:" + modeS + ", key:{" + algPerm + to_string(keyPerm) + ", " + algSub +
            to_string(keySub) + ", " + algDiff + to_string(keyDiff) + "}";
}

