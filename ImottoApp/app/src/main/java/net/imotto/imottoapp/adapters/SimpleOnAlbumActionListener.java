package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.activities.UserInfoActivity;
import net.imotto.imottoapp.services.models.AlbumModel;
import net.imotto.imottoapp.utils.Constants;

/**
 * Created by sunht on 16/11/8.
 */

public class SimpleOnAlbumActionListener implements OnAlbumItemActionListener {

    private Context context;
    public SimpleOnAlbumActionListener(Context ctx){
        this.context = ctx;
    }

    @Override
    public void onShowUserAction(AlbumModel m) {
        if(m.UID.equals(ImottoApplication.getInstance().getUserId())){
            return;
        }
        Intent intent = new Intent(context, UserInfoActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_UID, m.UID);
        bundle.putString(Constants.BUNDLE_UNAME, m.UserName);
        intent.putExtras(bundle);

        context.startActivity(intent);
    }
}
