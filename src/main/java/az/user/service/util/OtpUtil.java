package az.user.service.util;

public class OtpUtil {

    public static final int OTP_EXPIRE_MINUTES = 10;

    private OtpUtil() {
    }

    public static String generateOtp() {
        int otp = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(otp);
    }
}
