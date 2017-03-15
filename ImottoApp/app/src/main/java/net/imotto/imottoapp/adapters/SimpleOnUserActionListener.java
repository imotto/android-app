package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import net.imotto.imottoapp.activities.UserFollowersActivity;
import net.imotto.imottoapp.activities.UserFollowsActivity;
import net.imotto.imottoapp.activities.UserMottoActivity;
import net.imotto.imottoapp.controls.ActionSheet;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.ApiResp;
import net.imotto.imottoapp.services.models.RelatedUserModel;
import net.imotto.imottoapp.utils.Constants;

/**
 * Created by sunht on 16/11/22.
 *
 */

public class SimpleOnUserActionListener implements OnUserItemActionListener, ActionSheet.MenuItemClickListener {
    private static final int ACTION_SHEET_UNLOVE_AND_BAN=1;
    private static final int ACTION_SHEET_LOVE_AND_UNBAN=2;
    private static final int ACTION_SHEET_LOVE_AND_BAN=3;
    private Context context;
    private RelatedUserModel currentModel;
    private int currentActionSheet=0;


    public SimpleOnUserActionListener(Context ctx){
        context = ctx;
    }

    @Override
    public void onLoveAction(RelatedUserModel model) {
        //Toast.makeText(context, "love", Toast.LENGTH_SHORT).show();

        String relation="";
        if (model.RelationState == 1){
            relation ="TA是你喜欢的人，你想要将TA：";
        }else if(model.RelationState == 2){
            relation="TA喜欢你，你想要将TA：";
        }else if(model.RelationState == 3){
            relation = "你和TA互相喜欢，你想要将TA：";
        }else if(model.RelationState == 6){
            relation="TA喜欢你，却呆在你的黑名单里，你想要将TA：";
        }

        currentModel = model;
        ActionSheet menuView = new ActionSheet(context);
        menuView.setTitle(relation);
        menuView.setCancelButtonTitle("取消");// before add items

        if((model.RelationState & 1) == 1){
            menuView.addItems("移出喜欢的人", "加入黑名单");
            currentActionSheet = ACTION_SHEET_UNLOVE_AND_BAN;
        }else if((model.RelationState & 4) == 4){
            menuView.addItems("添加为喜欢的人", "移出黑名单");
            currentActionSheet = ACTION_SHEET_LOVE_AND_UNBAN;
        }else{
            menuView.addItems("添加为喜欢的人", "加入黑名单");
            currentActionSheet = ACTION_SHEET_LOVE_AND_BAN;
        }


        menuView.setItemClickListener(this);
        menuView.setCancelableOnTouchMenuOutside(true);
        menuView.showMenu();

    }

    @Override
    public void onItemClick(int itemPosition) {
        if(currentModel == null){
            return;
        }

        if((currentActionSheet == ACTION_SHEET_LOVE_AND_BAN && itemPosition == 0)
                ||(currentActionSheet == ACTION_SHEET_LOVE_AND_UNBAN && itemPosition == 0)) {
            //love user
            ImottoApi.getInstance().followUser(currentModel.ID, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                @Override
                public void onApiCallCompletion(ApiResp result) {
                    if(result.isSuccess()){
                        Toast.makeText(context, "add love success.", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, result.Msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public Class<ApiResp> getGenericClass() {
                    return ApiResp.class;
                }
            });


        }else if((currentActionSheet == ACTION_SHEET_LOVE_AND_BAN && itemPosition == 1)
                ||(currentActionSheet == ACTION_SHEET_UNLOVE_AND_BAN && itemPosition == 1)){
            //ban user

            ImottoApi.getInstance().addBanUser(currentModel.ID, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                @Override
                public void onApiCallCompletion(ApiResp result) {
                    if(result.isSuccess()){
                        Toast.makeText(context, "ban user success", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, result.Msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public Class<ApiResp> getGenericClass() {
                    return ApiResp.class;
                }
            });


        }else if(currentActionSheet == ACTION_SHEET_LOVE_AND_UNBAN && itemPosition == 1){
            //unban user
            ImottoApi.getInstance().removeBanUser(currentModel.ID, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                @Override
                public void onApiCallCompletion(ApiResp result) {
                    if(result.isSuccess()){
                        Toast.makeText(context, "unban user success", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context,result.Msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public Class<ApiResp> getGenericClass() {
                    return ApiResp.class;
                }
            });

        }else if(currentActionSheet == ACTION_SHEET_UNLOVE_AND_BAN && itemPosition == 0) {
            //unlove user
            ImottoApi.getInstance().unfollowUser(currentModel.ID, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                @Override
                public void onApiCallCompletion(ApiResp result) {
                    if(result.isSuccess()){
                        Toast.makeText(context, "unlove user success", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, result.Msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public Class<ApiResp> getGenericClass() {
                    return ApiResp.class;
                }
            });
        }
    }

    @Override
    public void onShowFollowersAction(RelatedUserModel model) {
        Intent intent = new Intent(context, UserFollowersActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_UID, model.ID);
        bundle.putString(Constants.BUNDLE_UNAME, model.UserName);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onShowFollowsAction(RelatedUserModel model) {
        Intent intent = new Intent(context, UserFollowsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_UID, model.ID);
        bundle.putString(Constants.BUNDLE_UNAME, model.UserName);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onShowMottosAction(RelatedUserModel model) {
        Intent intent = new Intent(context, UserMottoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_UID, model.ID);
        bundle.putString(Constants.BUNDLE_UNAME, model.UserName);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onShowRankAction(RelatedUserModel model) {
        Toast.makeText(context, String.format("%s的累计积分为%d。", model.DisplayName, model.Revenue),
                Toast.LENGTH_SHORT).show();
    }
}
