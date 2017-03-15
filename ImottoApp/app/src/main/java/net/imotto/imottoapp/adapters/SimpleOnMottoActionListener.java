package net.imotto.imottoapp.adapters;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.R;
import net.imotto.imottoapp.activities.CollectMottoActivity;
import net.imotto.imottoapp.activities.ReviewActivity;
import net.imotto.imottoapp.activities.UserInfoActivity;
import net.imotto.imottoapp.activities.VotesActivity;
import net.imotto.imottoapp.controls.ActionSheet;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.ApiResp;
import net.imotto.imottoapp.services.models.MottoModel;
import net.imotto.imottoapp.utils.Constants;
import net.imotto.imottoapp.utils.DateHelper;

import java.io.File;


/**
 * Created by sunht on 16/11/7.
 */

public class SimpleOnMottoActionListener implements OnMottoItemActionListener, ActionSheet.MenuItemClickListener {
    private static final String TAG = "MottoActionListener";
    private Context activity;
    private MottoModel currentModel;

    public SimpleOnMottoActionListener(Context act){
        this.activity = act;
    }

    public boolean checkLogin(){
        if(ImottoApplication.getInstance().isUserLogin()){
            return true;
        }
        Toast.makeText(activity, "请先登录后再执行此操作", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onLoveAction(final MottoModel m, final MottoItemViewHolder holder) {
        if(checkLogin()){
            int theDay = DateHelper.extractTheDay(m.AddTime);
            Log.i(TAG, "the day is :" + theDay);
            if(m.Loved == 1){
                //取消喜欢
                ImottoApi.getInstance().unloveMotto(m.ID, DateHelper.extractTheDay(m.AddTime), new ImottoApi.InvokeCompletionHandler<ApiResp>(){
                    @Override
                    public void onApiCallCompletion(ApiResp result) {
                        if(result.isSuccess()) {
                            Toast.makeText(activity, "取消喜欢成功", Toast.LENGTH_SHORT).show();
                            holder.btnLove.setImageResource(R.drawable.btn_love);
                            m.Loved = 0;
                            m.Loves = m.Loves - 1;
                            holder.lblLove.setText(Integer.toString(m.Loves));
                        }
                        else{
                            Toast.makeText(activity, result.Msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public Class<ApiResp> getGenericClass() {
                        return ApiResp.class;
                    }
                });
            }else{
                //设为喜欢
                ImottoApi.getInstance().loveMotto(m.ID, DateHelper.extractTheDay(m.AddTime), new ImottoApi.InvokeCompletionHandler<ApiResp>(){
                    @Override
                    public void onApiCallCompletion(ApiResp result) {
                        if(result.isSuccess()) {
                            Toast.makeText(activity, "添加喜欢成功", Toast.LENGTH_SHORT).show();
                            holder.btnLove.setImageResource(R.drawable.btn_love_on);
                            m.Loves = m.Loves + 1;
                            m.Loved = 1;
                            holder.lblLove.setText(Integer.toString(m.Loves));
                        }
                        else{
                            Toast.makeText(activity, result.Msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public Class<ApiResp> getGenericClass() {
                        return ApiResp.class;
                    }
                });

            }
        }
    }

    @Override
    public void onShowVotesAction(MottoModel m) {
        Intent intent = new Intent(activity, VotesActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.BUNDLE_MID, m.ID);
        intent.putExtras(bundle);

        activity.startActivity(intent);
    }

    @Override
    public void onMoreAction(MottoModel m) {
        currentModel = m;
        ActionSheet menuView = new ActionSheet(activity);
        menuView.setCancelButtonTitle("取消");// before add items
        menuView.addItems("复制到剪贴板", "分享到...", "举报不良信息");
        menuView.setItemClickListener(this);
        menuView.setCancelableOnTouchMenuOutside(true);
        menuView.showMenu();
    }

    @Override
    public void onItemClick(int itemPosition) {
        if(currentModel == null){
            return;
        }
        switch (itemPosition){
            case 0: //复制
                    // 从API11开始android推荐使用android.content.ClipboardManager
                    // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
                    ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本内容放到系统剪贴板里。
                    cm.setPrimaryClip(ClipData.newPlainText(null, currentModel.Content + "["+ currentModel.UserName+ "@偶得]"));
                    Toast.makeText(activity, "复制到剪贴板成功", Toast.LENGTH_LONG).show();
                break;
            case 1: //分享
                shareMsg("分享偶得","偶得", currentModel.Content + "[" + currentModel.UserName +"@偶得]", null);
                break;
            case 2: //举报
                //ImottoApi.getInstance().addReport(""+currentModel.ID,);
                final EditText et=new EditText(activity);

                new AlertDialog.Builder(activity).setTitle("填写举报原因").setView(et).
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1){
                                String reason=et.getText().toString();
                                Log.i(TAG,reason);
                                ImottoApi.getInstance().addReport("" + currentModel.ID, 0, reason, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                                    @Override
                                    public void onApiCallCompletion(ApiResp result) {
                                        if(result.isSuccess()){
                                            Toast.makeText(activity, "举报信息已提交，我们会及时处理并向您反馈处理结果。",Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(activity, result.Msg, Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public Class<ApiResp> getGenericClass() {
                                        return ApiResp.class;
                                    }
                                });
                            }
                        }).setNegativeButton("取消",null).show();
                break;
            default:
                break;
        }
    }

    private void shareMsg(String activityTitle, String msgTitle, String msgText,
                         String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/jpg");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(Intent.createChooser(intent, activityTitle));
    }

    @Override
    public void onReviewAction(MottoModel m) {
        Intent intent = new Intent(activity, ReviewActivity.class);

        Bundle bundle = new Bundle();
        bundle.putLong(Constants.BUNDLE_MID, m.ID);
        bundle.putInt(Constants.BUNDLE_THEDAY, DateHelper.extractTheDay(m.AddTime));
        intent.putExtras(bundle);

        activity.startActivity(intent);
    }


    @Override
    public void onMarkAction(MottoModel m) {

        if(checkLogin()) {
            Intent intent = new Intent(activity, CollectMottoActivity.class);

            Bundle bundle = new Bundle();
            bundle.putLong(Constants.BUNDLE_MID, m.ID);
            intent.putExtras(bundle);

            activity.startActivity(intent);
        }
    }

    @Override
    public void onShowUserAction(MottoModel m) {
        if(m.UID.equals(ImottoApplication.getInstance().getUserId())){
            return;
        }
        Intent intent = new Intent(activity, UserInfoActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_UID, m.UID);
        bundle.putString(Constants.BUNDLE_UNAME, m.UserName);
        intent.putExtras(bundle);

        activity.startActivity(intent);

    }

    @Override
    public void onVoteAction(final MottoModel m, final MottoItemViewHolder holder, final int vote) {

        if(checkLogin()){
            int theDay = DateHelper.extractTheDay(m.AddTime);
            ImottoApi.getInstance().addVote(m.ID, theDay, vote, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                @Override
                public void onApiCallCompletion(ApiResp result) {
                    if (result.isSuccess()){
                        Toast.makeText(activity, "投票成功", Toast.LENGTH_SHORT).show();

                        if(vote == 1){
                            m.Up += 1;
                            //holder.btnUp.setImageResource(R.drawable.ic_plus_empty_actived);
                            m.Vote = 1;
                        }else if (vote == -1){
                            m.Down += 1;
                            //holder.btnDown.setImageResource(R.drawable.ic_minus_empty_actived);
                            m.Vote = -1;
                        }else{
                            m.Vote = 0;
                        }

                        holder.refreshVoteState(m);
                    }
                    else{
                        Toast.makeText(activity, result.Msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public Class<ApiResp> getGenericClass() {
                    return ApiResp.class;
                }
            });
        }
    }
}
