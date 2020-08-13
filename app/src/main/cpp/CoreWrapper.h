#ifndef ANDROID_CORE_WRAPPER_H
#define ANDROID_CORE_WRAPPER_H

#include <jni.h>

extern "C" {

    JNIEXPORT jobject JNICALL
    Java_com_nyahonk_odfreader_domain_CoreWrapper_parseNative(JNIEnv *env, jobject instance, jobject options);

    JNIEXPORT jobject JNICALL
    Java_com_nyahonk_odfreader_domain_CoreWrapper_backtranslateNative(JNIEnv *env, jobject instance, jobject options, jstring htmlDiff);

    JNIEXPORT void JNICALL
    Java_com_nyahonk_odfreader_domain_CoreWrapper_closeNative(JNIEnv *env, jobject instance, jobject options);
}

#endif //ANDROID_CORE_WRAPPER_H