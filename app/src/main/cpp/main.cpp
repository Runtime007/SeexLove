//
// Created by Administrator on 2016/12/17.
//
#include <android/log.h>
#include <jni.h>
#include <pthread.h>

#include "IAgoraMediaEngine.h"
#include "IAgoraRtcEngine.h"
#include <unistd.h>

JavaVM* javaVM;
JNIEnv* env;

jclass renderClass;
jmethodID fuRenderToNV21ImageMethod;

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	javaVM = vm;
	JNIEnv* env;
	javaVM->AttachCurrentThread(&env, NULL);

	//__android_log_print(ANDROID_LOG_ERROR, "踩你妈", "before");

	renderClass = env->FindClass("com/faceunity/Render");
	//    __android_log_print(ANDROID_LOG_ERROR, "踩你妈", "after");
	renderClass = (jclass)env->NewGlobalRef(renderClass);

	return JNI_VERSION_1_4;
}

class AgoraVideoFrameObserver : public agora::media::IVideoFrameObserver
{
public:

	int iscopy;
public:
	char* yBuffer1;  //Y data buffer
	char* uBuffer1;  //U data buffer
	char* vBuffer1;  //V data buffer
	int width1;
	int height1;
	int st_y;
	int st_u;
	int st_v;
	int getVideoFrame(int iscopy1)
	{
		iscopy = iscopy1;
		return 1;
	}
public:
	bool onCaptureVideoFrame(VideoFrame& videoFrame)
	{
		//    __android_log_print(ANDROID_LOG_ERROR, "踩你妈onCaptureVideoFrame", "1");
		javaVM->AttachCurrentThread(&env, NULL);
		//  __android_log_print(ANDROID_LOG_ERROR, "踩你妈onCaptureVideoFrame", "2");
		fuRenderToNV21ImageMethod = env->GetStaticMethodID(renderClass, "fuRenderToNV21Image", "([BII)V");
		//  __android_log_print(ANDROID_LOG_ERROR, "踩你妈onCaptureVideoFrame", "3");
		jsize len = videoFrame.width * videoFrame.height * 3 / 2;
		jbyteArray array = env->NewByteArray(len);
		jbyte buf[len];
		int yLength = videoFrame.width * videoFrame.height;
		int i = 0;
		for (; i < yLength; i++) {
			buf[i] = ((jbyte *)videoFrame.yBuffer)[i];
		}
		int uLength = yLength / 4;
		for (int j = 0; j < uLength; i += 2, j++) {
			buf[i] = ((jbyte *)videoFrame.vBuffer)[j];
			buf[i + 1] = ((jbyte *)videoFrame.uBuffer)[j];
		}
		env->SetByteArrayRegion(array, 0, len, buf);

		env->CallStaticVoidMethod(renderClass, fuRenderToNV21ImageMethod, array, videoFrame.width, videoFrame.height);

		env->GetByteArrayRegion(array, 0, len, buf);
		for (i = 0; i < yLength; i++) {
			((jbyte *)videoFrame.yBuffer)[i] = buf[i];
		}
		for (int j = 0; j < uLength; i += 2, j++) {
			((jbyte *)videoFrame.vBuffer)[j] = buf[i];
			((jbyte *)videoFrame.uBuffer)[j] = buf[i + 1];
		}

		env->DeleteLocalRef(array);

		javaVM->DetachCurrentThread();

		return true;
	}
	bool onRenderVideoFrame(unsigned int uid, VideoFrame& videoFrame)
	{
		if (iscopy == 2) {
			free(yBuffer1);
			free(uBuffer1);
			free(vBuffer1);

			yBuffer1 = (char *)malloc(videoFrame.yStride*videoFrame.height* sizeof(char));
			uBuffer1 = (char *)malloc(videoFrame.uStride*videoFrame.height / 2 * sizeof(char));
			vBuffer1 = (char *)malloc(videoFrame.vStride*videoFrame.height / 2 * sizeof(char));

			memset(yBuffer1, 0x00, videoFrame.yStride*videoFrame.height* sizeof(char));
			memset(uBuffer1, 0x00, videoFrame.uStride*videoFrame.height* sizeof(char) / 2);
			memset(vBuffer1, 0x00, videoFrame.vStride*videoFrame.height* sizeof(char) / 2);

			width1 = videoFrame.width;
			height1 = videoFrame.height;


			memcpy(yBuffer1, videoFrame.yBuffer, videoFrame.yStride*videoFrame.height);
			memcpy(uBuffer1, videoFrame.uBuffer, videoFrame.uStride*videoFrame.height / 2);
			memcpy(vBuffer1, videoFrame.vBuffer, videoFrame.vStride*videoFrame.height / 2);
			st_y = videoFrame.yStride;
			st_u = videoFrame.uStride;
			st_v = videoFrame.vStride;


			iscopy = 0;


		}
		return true;
	}

};

static agora::rtc::IRtcEngine* rtcEngine = NULL;

class getVideoFrame
{
	struct {
		char * yBuffer;
		char * uBuffer;
		char * vBuffer;
		int width;
		int height;
	}YUV;
	AgoraVideoFrameObserver s_videoFrameObserver;
	//    int  initregist();
	//    int copyVideoFrame(bool islocal ,int w,int h,char* outpicdata);
public:
	getVideoFrame* initregist()
	{


		agora::util::AutoPtr<agora::media::IMediaEngine> mediaEngine;



		//agora::media::IMediaEngine *p=(agora::media::IMediaEngine*)p1;
		mediaEngine.queryInterface(*rtcEngine,agora::rtc::AGORA_IID_MEDIA_ENGINE);

		__android_log_print(ANDROID_LOG_ERROR, "lobo", "*********");
		if (mediaEngine)
		{
			mediaEngine->registerVideoFrameObserver(&s_videoFrameObserver);

			return this;
		}

		return NULL;
	}
	//    int copyVideoFrame(bool islocal, int *w, int *h,  char **y, char ** u , char **v)
	int copyVideoFrame(bool islocal, int *w, int *h, char **y, char ** u, char **v, int *st_y, int *st_u, int* st_v)
	{
		if (islocal)
		{

			s_videoFrameObserver.iscopy = 1;
		}
		else
		{

			s_videoFrameObserver.iscopy = 2;
		}
		while (1) {

			if (s_videoFrameObserver.iscopy == 0){
				*y = s_videoFrameObserver.yBuffer1;
				*u = s_videoFrameObserver.uBuffer1;
				*v = s_videoFrameObserver.vBuffer1;
				*w = s_videoFrameObserver.width1;
				*h = s_videoFrameObserver.height1;
				*st_y = s_videoFrameObserver.st_y;
				*st_u = s_videoFrameObserver.st_u;
				*st_v = s_videoFrameObserver.st_v;


				break;
			}

			sleep(1);
		}



		return 1;
	}

};
AgoraVideoFrameObserver s_videoFrameObserver;

extern "C" __attribute__((visibility("default"))) int loadAgoraRtcEnginePlugin(agora::rtc::IRtcEngine* engine)
{
	rtcEngine = engine;
	// __android_log_print(ANDROID_LOG_ERROR, "踩你妈onCaptureVideoFrame", "8");
	agora::util::AutoPtr<agora::media::IMediaEngine> mediaEngine;
	mediaEngine.queryInterface(*engine, agora::rtc::AGORA_IID_MEDIA_ENGINE);

	if (mediaEngine)
	{
		//     __android_log_print(ANDROID_LOG_ERROR, "踩你妈onCaptureVideoFrame", "9");
		mediaEngine->registerVideoFrameObserver(&s_videoFrameObserver);
	}
	return 0;
}


extern "C" void __attribute__((visibility("default"))) unloadAgoraRtcEnginePlugin(agora::rtc::IRtcEngine* engine)
{
	//    __android_log_print(ANDROID_LOG_ERROR, "踩你妈onCaptureVideoFrame", "10");
	rtcEngine = NULL;
}
extern "C" {
	JNIEXPORT jobject JNICALL Java_company_chat_coquettish_android_shotimg_CutOut_getyuv(
		JNIEnv* env, jclass thiz, jlong p, jobject diskobj, jboolean type);
	JNIEXPORT jlong JNICALL Java_company_chat_coquettish_android_shotimg_CutOut_init(
		JNIEnv* env, jclass thiz, jlong p1);
	JNIEXPORT jlong JNICALL Java_company_chat_coquettish_android_shotimg_CutOut_etyuvs(
		JNIEnv* env, jclass thiz, jlong p2);
}
JNIEXPORT jlong JNICALL Java_company_chat_coquettish_android_shotimg_CutOut_init(
	JNIEnv* env, jclass thiz, jlong p1){
	getVideoFrame *p = new getVideoFrame();
	p->initregist();
	return (jlong)p;
}
JNIEXPORT jlong JNICALL Java_company_chat_coquettish_android_shotimg_CutOut_etyuvs(
	JNIEnv* env, jclass thiz, jlong p2) {
	return p2;
}

JNIEXPORT jobject JNICALL Java_company_chat_coquettish_android_shotimg_CutOut_getyuv(
	JNIEnv* env, jclass thiz, jlong p1, jobject diskobj, jboolean type) {
	getVideoFrame*p = (getVideoFrame*)p1;

	//获取Java中的实例类
	jclass objectClass = env->FindClass("company/chat/coquettish/android/shotimg/YUV");

	jmethodID id_date = env->GetMethodID(objectClass, "<init>", "()V");
	jobject now = env->NewObject(objectClass, id_date);



	//获取类中每一个变量的定义
	jfieldID y1 = env->GetFieldID(objectClass, "y", "[B");
	jfieldID u1 = env->GetFieldID(objectClass, "u", "[B");
	jfieldID v1 = env->GetFieldID(objectClass, "v", "[B");
	jfieldID width = env->GetFieldID(objectClass, "width", "I");
	jfieldID height = env->GetFieldID(objectClass, "height", "I");


	//    jfieldID y1 = env->GetFieldID( objectClass,"getY","[B");
	//    jfieldID u1 = env->GetFieldID( objectClass,"getU","[B");
	//    jfieldID v1 = env->GetFieldID( objectClass,"getV","[B");
	//    jmethodID width2 = env->GetMethodID( objectClass,"getWidth","()I");
	//    jmethodID height2 = env->GetMethodID( objectClass,"getHeight","()I");
	//
	//    jint ww = env->CallIntMethod(now,width2);
	//    jint hh = env->CallIntMethod(now,height2);

	//    __android_log_print(ANDROID_LOG_ERROR, "aaaaa99999", "hh:%d", hh);

	char*y;
	char*u;
	char*v;
	int w;
	int h;
	int st_y, st_u, st_v;
	p->copyVideoFrame(type, &w, &h, &y, &u, &v, &st_y, &st_u, &st_v);



	//    __android_log_print(ANDROID_LOG_ERROR, "aaaaa44444444444", "y:%ld/r/n", *y);
	//    __android_log_print(ANDROID_LOG_ERROR, "aaaaa44444444444", "u:%ld/r/n", *u);
	//    __android_log_print(ANDROID_LOG_ERROR, "aaaaa44444444444", "v:%ld/r/n", *v);



	//    FILE* f = fopen("/storage/emulated/0/11111aaaaa.txt", "wb");
	//           	            	                		if (f == NULL) {
	//           	            	                			return 0;
	//           	            	                		}
	//           	            	                		fwrite(y, st_y*h* sizeof(char), 1, f);
	//           	            	                	    fclose (f);

	//    __android_log_print(ANDROID_LOG_ERROR, "aaaaa44444444444", "w:%d", w);
	//    __android_log_print(ANDROID_LOG_ERROR, "aaaaa44444444444", "h:%d", h);



	jbyteArray jarrayy = env->NewByteArray(st_y*h);
	jbyte *jby = env->GetByteArrayElements(jarrayy, 0);
	memcpy(jby, y, st_y*h);
	env->SetByteArrayRegion(jarrayy, 0, st_y*h, jby);
	//env->SetByteArrayRegion(jarrayy, 0, st_y*h , (jbyte*)y);
	env->SetObjectField(now, y1, jarrayy);

	jbyteArray jarrayu = env->NewByteArray(st_u*h / 2);
	jbyte *jbu = env->GetByteArrayElements(jarrayu, 0);
	memcpy(jbu, u, st_u*h / 2);
	env->SetByteArrayRegion(jarrayu, 0, st_u*h / 2, jbu);
	env->SetObjectField(now, u1, jarrayu);

	jbyteArray jarrayv = env->NewByteArray(st_v*h / 2);
	jbyte *jbv = env->GetByteArrayElements(jarrayv, 0);
	memcpy(jbv, v, st_v*h / 2);
	env->SetByteArrayRegion(jarrayv, 0, st_v*h / 2, jbv);
	//env->SetByteArrayRegion( jarrayv, 0, st_v*h/4, (jbyte*)v);
	env->SetObjectField(now, v1, jarrayv);

	env->SetIntField(now, width, w);
	env->SetIntField(now, height, h);


	free(y);
	free(u);
	free(v);

	return now;
}