package com.iranplanner.tourism.iranplanner.ui.activity.forgetPassword;


import com.iranplanner.tourism.iranplanner.di.data.component.NetComponent;
import com.iranplanner.tourism.iranplanner.di.model.CustomScope;
import com.iranplanner.tourism.iranplanner.ui.activity.login.LoginActivity;
import com.iranplanner.tourism.iranplanner.ui.activity.login.LoginModule;
import com.iranplanner.tourism.iranplanner.ui.fragment.LoginFragment;

import dagger.Component;

/**
 * Created by Hoda on 11-May-16.
 */
@CustomScope
@Component(dependencies = NetComponent.class, modules = {ForgetPasswordModule.class})
public interface ForgetComponent {
    void inject(ForgetPasswordActivity forgetPasswordActivity);
}



