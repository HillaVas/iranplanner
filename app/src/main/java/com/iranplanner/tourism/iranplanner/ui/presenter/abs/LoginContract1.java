package com.iranplanner.tourism.iranplanner.ui.presenter.abs;

import com.iranplanner.tourism.iranplanner.ui.presenter.Presenter;

import entity.GoogleLoginReqSend;
import entity.LoginReqSend;
import entity.LoginResult;


/**
 * Created by Hoda
 */
public abstract class LoginContract1 extends Presenter<LoginContract1.View> {
    public interface View {
        void showLoginResult(LoginResult loginResult);

        void showError(String message);

        void showComplete();

        void showProgress();

        void dismissProgress();
    }


    public abstract void getLoginResult(String action,
                                        String email,
                                        String password,
                                        String token,
                                        String androidId);

    public abstract void getLoginPostResul(LoginReqSend request, String cid,
                                           String androidId);
    public abstract void getGoogleLoginPostResult(GoogleLoginReqSend GoogleLoginReqSend, String cid,
                                                  String androidId);
}