#include <jni.h>
#include <string>

#include "ChaoticCypher.h"


bytes asByteVector(JNIEnv *env, jbyteArray src) {
    size_t size = env->GetArrayLength(src);
    auto *buf = new byte[size];
    bytes res;
    env->GetByteArrayRegion(src, 0, (jsize) size, reinterpret_cast<jbyte *>(buf));
    for (int i = 0; i < size; ++i) {
        res.push_back(buf[i]);
    }
    delete[] buf;
//    LOGD("%s", byte2hex(res).c_str());
    return res;
}


jbyteArray asJByteArray(JNIEnv *env, const bytes &data) {
//    LOGD("%s", byte2hex(data).c_str());
    auto *buff = (jbyte *) data.data();
    auto size = static_cast<jsize>(data.size());
    jbyteArray res = env->NewByteArray(size);
    env->SetByteArrayRegion(res, 0, size, buff);
    return res;
}

std::unique_ptr<ChaoticMap> getType(JNIEnv *pEnv, jobject pJobject) {
    if (pJobject == nullptr) {
        return ChaoticMap::create(0);
    }

    jmethodID getTypeMethod = pEnv->GetMethodID(pEnv->FindClass("com/karry/chaotic/ChaoticType"),
                                                "getType", "()I");
    jint type = pEnv->CallIntMethod(pJobject, getTypeMethod);
//    LOGD("%d", reinterpret_cast<int>(type));
    return ChaoticMap::create(type);
}

ChaoticCypher getCypher(JNIEnv *env, jobject permAlgm, jobject subAlgm, jobject diffAlgm) {
    return ChaoticCypher(
            getType(env, permAlgm),
            getType(env, subAlgm),
            getType(env, diffAlgm)
    );
}


extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_karry_chaotic_NativeLib_encrypt(
        JNIEnv *env,
        jobject /* this */,
        jbyteArray dataJava,
        jbyteArray keyJava,
        jobject permAlgm,
        jobject subAlgm,
        jobject diffAlgm
) {
    auto data = asByteVector(env, dataJava);
    auto key = asByteVector(env, keyJava);
    auto cypher = getCypher(env, permAlgm, subAlgm, diffAlgm);
    cypher.init(ChaoticCypher::ENCRYPT_MODE, key);
    LOGD("NativeLib", "%s", cypher.info().c_str());

    return asJByteArray(env, cypher.doFinal(data));
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_karry_chaotic_NativeLib_decrypt(
        JNIEnv *env,
        jobject /* this */,
        jbyteArray dataJava,
        jbyteArray keyJava,
        jobject permAlgm,
        jobject subAlgm,
        jobject diffAlgm
) {
    auto data = asByteVector(env, dataJava);
    auto key = asByteVector(env, keyJava);
    auto cypher = getCypher(env, permAlgm, subAlgm, diffAlgm);
    cypher.init(ChaoticCypher::DECRYPT_MODE, key);
    LOGD("NativeLib", "%s", cypher.info().c_str());

    return asJByteArray(env, cypher.doFinal(data));
}