package com.ecomerce.sell.excepotions;

import lombok.Getter;

@Getter
public enum DAOResponse {
    USER_NOT_FOUND("VHR_ERROR_05", "User not found"),
    SUCCESS("VHR_SUCCESS_00", "Success"),
    INVALID_REQUEST("VHR_ERROR_97", "Invalid request parameters"),
    SYSTEM_ERROR("VHR_ERROR_98","System Under Maintenance" ),
    NO_DATA_FOUND("VHR_ERROR_2","No Data Found" ),
    USER_NOT_ACTIVE("VHR_ERROR_06", "User is not active"),
    INVALID_CREDENTIALS("VHR_ERROR_07", "Invalid credentials"),
    ALREADY_EXISTS("VHR_ERROR_08", "Already exists"),
    UNAUTHORIZED("VHR_ERROR_09", "Unauthorized access"),;

  kEYS
//    #pk_test_51PI4J0RpVeu5Nab9nUra38Gv9qD5N8HU39fO8du0XQlZuIST1X3xH8zinqSebISwxe84QQJUtmpk0uMkaj70LYQd00tBOOcQoN
//#sk_test_51PI4J0RpVeu5Nab9t29yZO7xzbcZct0RKSQHxPJG4q5DQ5wrGPMjT3T16FzJYQtTtZAm1sNZUxas90cFA3GHmDfW005tKBNiMN
//#whsec_23a78ccf3d1cf56206bc32100e94d4095cc6038789fda3960fdc4cd9de4805cd

    private final String code;
    private final String message;

    DAOResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
