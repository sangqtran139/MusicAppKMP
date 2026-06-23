# R8 rules cho release. Phần lớn thư viện (Ktor, OkHttp, Coil, SQLDelight) đã kèm consumer
# rules; ở đây chỉ bổ sung cho kotlinx.serialization (model @Serializable + route Navigation)
# và vài keep phòng thủ. Xem docs/Setup khi đổi.

# --- kotlinx.serialization ---
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

# Giữ Companion + serializer() của lớp @Serializable (DTO catalog + route Navigation type-safe).
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static ** INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class **$$serializer { *; }

# Generated serializers cho @Serializable object (route data object/class).
-keep,includedescriptorclasses class com.sangtq.musicappkmp.**$$serializer { *; }

# --- Coroutines (giữ field volatile nội bộ để tránh cảnh báo R8) ---
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
