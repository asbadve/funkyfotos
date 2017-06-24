package com.appfest.funkyfotos.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.appfest.funkyfotos.BasePresenter;
import com.appfest.funkyfotos.BaseView;
import com.appfest.funkyfotos.dto.User;

/**
 * Created by kaushald on 22/01/17.
 */

public interface ReqAccessContract {

    interface View extends BaseView<Presenter> {
        void onNameError(String message);
        void onUsernameError(String message);
        void onPasswordError(String message);
        void onRelationshipEmptyError();
        void onRequestSentSuccessfully();
        void onAlreadyRegistered();
        void onImageAvailable(Bitmap image);
        void onAlreadyGranted();
        void onError(String displayMessage);
        void showLoadingIndicator();
        void hideLoadingIndicator();
        void onEmptyUserNameError();
        void onNoProfilePicError();
    }

    interface Presenter extends BasePresenter {
        void launchImagePicker();

        void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
                int[] grantResults);

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void handleAccessRequest(User user);
    }
}
