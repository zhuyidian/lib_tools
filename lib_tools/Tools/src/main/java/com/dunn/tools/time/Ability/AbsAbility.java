package com.dunn.tools.time.Ability;

import android.content.Context;

import com.dunn.tools.time.bean.RemoteCommand;


/**
 * @ClassName: AbsAbility
 * @Author: XuZeXiao
 * @CreateDate: 4/1/21 11:38 AM
 * @Description:
 */
public abstract class AbsAbility implements IAbility {
    protected Context mContext;

    @Override
    public void init(Context context) {
        mContext = context;
    }

    @Override
    public void realMessage(RemoteCommand command) {

    }
}