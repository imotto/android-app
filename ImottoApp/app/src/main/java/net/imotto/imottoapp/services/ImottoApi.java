package net.imotto.imottoapp.services;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.services.models.AcquireVerifyCodeResp;
import net.imotto.imottoapp.services.models.AddExchangeInfoResp;
import net.imotto.imottoapp.services.models.ApiResp;
import net.imotto.imottoapp.services.models.CheckUpdateResp;
import net.imotto.imottoapp.services.models.LoginResp;
import net.imotto.imottoapp.services.models.PEResultModel;
import net.imotto.imottoapp.services.models.ReadAddressResp;
import net.imotto.imottoapp.services.models.ReadAlbumResp;
import net.imotto.imottoapp.services.models.ReadAwardeeResp;
import net.imotto.imottoapp.services.models.ReadAwardsResp;
import net.imotto.imottoapp.services.models.ReadBadgeResp;
import net.imotto.imottoapp.services.models.ReadBillRecordResp;
import net.imotto.imottoapp.services.models.ReadExchangeRecordResp;
import net.imotto.imottoapp.services.models.ReadGiftsResp;
import net.imotto.imottoapp.services.models.ReadMottoResp;
import net.imotto.imottoapp.services.models.ReadNoticeResp;
import net.imotto.imottoapp.services.models.ReadRecentTalkResp;
import net.imotto.imottoapp.services.models.ReadRelatedUserResp;
import net.imotto.imottoapp.services.models.ReadReviewResp;
import net.imotto.imottoapp.services.models.ReadScoreRecordResp;
import net.imotto.imottoapp.services.models.ReadTalkMsgResp;
import net.imotto.imottoapp.services.models.ReadUserRankResp;
import net.imotto.imottoapp.services.models.ReadUserResp;
import net.imotto.imottoapp.services.models.ReadVotesResp;
import net.imotto.imottoapp.services.models.RegisterDeviceResp;
import net.imotto.imottoapp.services.models.UserAddressModel;


import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sunht on 16/10/27.
 *
 */

public class ImottoApi {
    private static final String TAG = "ImottoApi";
    //开发
    private static final String ApiUrl = "http://app.imotto.net/api/%s";

    private static ImottoApi instance = new ImottoApi();
    private static AsyncHttpClient client = new AsyncHttpClient();
    private ImottoApi(){

    }

    public static ImottoApi getInstance() {
        return instance;
    }



    /**
     * 设备注册 CMN1001
    */
    public void registerDevice(String uuid, String screen, InvokeCompletionHandler<RegisterDeviceResp> completionHandler){

        RequestParams params = new RequestParams();
        params.add("Brand",android.os.Build.BRAND);// 手机品牌
        params.add("Model",android.os.Build.MODEL);// 手机型号
        params.add("OS" , "Android"); //操作系统
        params.add("OSVersion", android.os.Build.VERSION.RELEASE);//系统版本
        params.add("Screen", screen);
        params.add("Resolution", screen);
        params.add("Midu", "");
        params.add("UniqueId", uuid);
        params.add("OSId", android.os.Build.ID);
        params.add("TVersion", ImottoApplication.getInstance().getVersion());
        params.add("Type", "A");


        doRequest("CMN1001",params, completionHandler);
    }

    //检查更新
    public void checkUpdate(String sign, InvokeCompletionHandler<CheckUpdateResp> completionHandler){

        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("Type","A");
        params.add("Version" , "os");

        doRequest("CMN1002",params, completionHandler);
    }

    //获取验证码
    public void acquireVerifyCode(String mobile, int opcode, InvokeCompletionHandler<AcquireVerifyCodeResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("Mobile",mobile);
        params.add("OpCode" , ""+opcode);

        doRequest("CMN1003",params, completionHandler);
    }

    //加入黑名单 USR1001
    public void addBanUser(String targetUID, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("TargetUId" , targetUID);

        doRequest("USR1001",params, completionHandler);
    }

    //移出黑名单 USR1002
    public void removeBanUser(String targetUID, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("TargetUId" , targetUID);

        doRequest("USR1002",params, completionHandler);
    }

    //关注用户 USR1003
    public void followUser(String targetUID, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token",  ImottoApplication.getInstance().getUserToken());
        params.add("TargetUId", targetUID);

        doRequest("USR1003",params, completionHandler);
    }

    //取关用户 USR1004
    public void unfollowUser(String targetUID, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign",ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("TargetUId", targetUID);

        doRequest("USR1004",params, completionHandler);
    }

    //用户注册 USR1005
    public void registerUser(String mobile, String password, String userName,
                             String verifyCode, String inviteCode, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("Mobile", mobile);
        params.add("Password",password);
        params.add("UserName", userName);
        params.add("VerifyCode", verifyCode);
        params.add("InviteCode", inviteCode);

        doRequest("USR1005",params, completionHandler);
    }

    //用户登录 USR1006
    public void userLogin(String mobile, String password, InvokeCompletionHandler<LoginResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("Mobile", mobile);
        params.add("Password",password);

        doRequest("USR1006",params, completionHandler);
    }
    
    //用户登出 USR1007
    public void userLogout(InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());

        doRequest("USR1007",params, completionHandler);
        
    }

    //修改用户名称 usr1008
    public void modifyUserName(String userName, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("UserName",userName);

        doRequest("USR1008",params, completionHandler);
    }

    public void modifyThumb(File thumbFile, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        try {
            params.put("Thumb", thumbFile);
        }catch (FileNotFoundException e){
            e.printStackTrace();
            ApiResp resp = new ApiResp();
            resp.Code="USR1009";
            resp.Msg="找不到照片";
            resp.State = -1;
            completionHandler.onApiCallCompletion(resp);
        }

        doRequest("USR1009", params, completionHandler);

    }

    //修改用户密码 usr1010
    public void modifyPassword(String oldPass, String newPass, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("OldPassword", oldPass);
        params.add("NewPassword", newPass);

        doRequest("USR1010",params, completionHandler);
    }

    //修改用户性别
    public void modifySex(int sex, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("Sex", Integer.toString(sex));

        doRequest("USR1011",params, completionHandler);
    }

    ///发送消息
    public void sendMsg(String tuid, String content, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("TUID", tuid);
        params.add("Content", content);

        doRequest("USR2001", params, completionHandler);
    }

    ///将通知消息设为已读
    public void setNoticeRead(long nid, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("ID", Long.toString(nid));

        doRequest("USR2002", params, completionHandler);
    }

    ///重置登录密码
    public void resetPassword(String mobile, String vcode, String newPass,
                              InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("Mobile", mobile);
        params.add("VerifyCode", vcode);
        params.add("Password", newPass);

        doRequest("USR3001", params, completionHandler);
    }

    ///添加收货地址
    public void addAddress(UserAddressModel addr, InvokeCompletionHandler<AddExchangeInfoResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("Province", addr.Province);
        params.add("City", addr.City);
        params.add("District", addr.District);
        params.add("Address", addr.Address);
        params.add("Zip", addr.Zip);
        params.add("Contact", addr.Contact);
        params.add("Mobile",addr.Mobile);

        doRequest("USR4001", params, completionHandler);
    }

    ///添加关联账户
    public void addRelAccount(int platform, String accountNo, String accountName,
                              InvokeCompletionHandler<AddExchangeInfoResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("Platform", Integer.toString(platform));
        params.add("AccountNo", accountNo);
        params.add("AccountName", accountName);

        doRequest("USR4002", params, completionHandler);
    }

    ///准备进行礼品兑换
    public void prepareExchange(int giftId, int reqInfoType,
                                InvokeCompletionHandler<PEResultModel> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("GiftId", Integer.toString(giftId));
        params.add("ReqInfoType", Integer.toString(reqInfoType));

        doRequest("USR4004", params, completionHandler);

    }

    ///兑换礼品
    public void doExchange(int giftId, long reqInfoId, int amount,
                           InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("GiftId", Integer.toString(giftId));
        params.add("ReqInfoId", Long.toString(reqInfoId));
        params.add("Amount", Integer.toString(amount));

        doRequest("USR4005", params, completionHandler);
    }

    ///确认收货
    public void receiveGift(long exchangeId, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("ExchangeId", Long.toString(exchangeId));

        doRequest("USR4006", params, completionHandler);
    }

    ///评价礼品
    public void reviewGift(int giftId, long exchangeId, double rate, String comment, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("GiftId", Integer.toString(giftId));
        params.add("ExchangeId", Long.toString(exchangeId));
        params.add("Rate", Double.toString(rate));
        params.add("Comment", comment);

        doRequest("USR4007", params, completionHandler);
    }

    public void setAwardAddress(int awardId, long addrId, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("AwardId", Integer.toString(awardId));
        params.add("AddrId", Long.toString(addrId));

        doRequest("USR8001", params, completionHandler);
    }

    public void receiveAward(int awardId, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("AwardId", Integer.toString(awardId));

        doRequest("USR8002", params, completionHandler);
    }

    ///举报不良信息
    public void addReport(String tid, int type, String reason,
                          InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("TargetID", tid);
        params.add("Type", Integer.toString(type));
        params.add("Reason", reason);

        doRequest("USR9001", params, completionHandler);
    }

    // MARK: - Motto

    /// 添加Motto
    public void addMotto(int rid, String content, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("RID", Integer.toString(rid));
        params.add("Content", content);

        doRequest("MOT1001", params, completionHandler);
    }

    /**
     *  添加投票
     * @param mid  偶得ID
     * @param theDay 偶得发布日期 yyyyMMdd
     * @param vote   投票： 1:支持，-1：反对，0：中立
     * @param completionHandler
     */
    public void addVote(long mid, int theDay, int vote, InvokeCompletionHandler<ApiResp> completionHandler) {
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("MID", Long.toString(mid));
        params.add("TheDay", Integer.toString(theDay));
        params.add("Support", Integer.toString(vote));

        doRequest("MOT1002", params, completionHandler);
    }

    /// 喜欢Motto
    public void loveMotto(long mid, int theDay, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("MID", Long.toString(mid));
        params.add("TheDay", Integer.toString(theDay));

        doRequest("MOT1003", params, completionHandler);
    }

    /// 取消喜欢Motto
    public void unloveMotto(long mid, int theDay, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("MID", Long.toString(mid));
        params.add("TheDay", Integer.toString(theDay));

        doRequest("MOT1004", params, completionHandler);
    }

    /// 添加评论
    public void addReview(long mid, int theDay, String content, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("MID", Long.toString(mid));
        params.add("TheDay", Integer.toString(theDay));
        params.add("Content", content);

        doRequest("MOT1005", params, completionHandler);
    }

    ///删除评论
    public void removeReview(long mid, long rid, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("MID", Long.toString(mid));
        params.add("RID", Long.toString(rid));

        doRequest("MOT1006", params, completionHandler);
    }

    ///添加评论投票
    public void addReviewVote(long mid, long rid, boolean support, InvokeCompletionHandler<ApiResp> completionHandler) {
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("MID", Long.toString(mid));
        params.add("RID", Long.toString(rid));
        params.add("Support", Integer.toString(support ? 1 : 0));

        doRequest("MOT1007", params, completionHandler);
    }

    // MARK: - Collection(Album)

    ///创建珍藏
    public void addCollection(String title, String tags, String summary, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("Title", title);
        params.add("Tags", tags);
        params.add("Summary", summary);

        doRequest("MOT2001", params, completionHandler);
    }

    ///喜欢珍藏
    public void loveCollection(long cid, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("CID", Long.toString(cid));

        doRequest("MOT2002", params, completionHandler);
    }

    ///取消喜欢珍藏
    public void unloveCollection(long cid, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("CID", Long.toString(cid));

        doRequest("MOT2003", params, completionHandler);
    }

    ///将Motto加入珍藏
    public void addMottoToCollection(long mid, long cid, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("MID", Long.toString(mid));
        params.add("CID", Long.toString(cid));

        doRequest("MOT2004", params, completionHandler);
    }

    ///从珍藏中移除指定motto
    public void removeMottoFromCollection(long mid, long cid, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("MID", Long.toString(mid));
        params.add("CID", Long.toString(cid));

        doRequest("MOT2005", params, completionHandler);
    }

    ///创建珍藏
    public void updateCollection(long cid, String title, String tags, String summary, InvokeCompletionHandler<ApiResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("CID", Long.toString(cid));
        params.add("Title", title);
        params.add("Tags", tags);
        params.add("Summary", summary);

        doRequest("MOT2006", params, completionHandler);
    }


    // MARK: - 征集



    // MARK: - Readers

    ///按天读取Motto
    public void readMottos(int theday, int pIndex, int pSize, InvokeCompletionHandler<ReadMottoResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("TheDay", Integer.toString(theday));
        params.add("PIndex", Integer.toString(pIndex));
        params.add("PSize", Integer.toString(pSize));

        doRequest("RED2001", params, completionHandler);
    }

    ///查询motto的相关评论
    public void readReviews(long mid, int pIndex, int pSize, InvokeCompletionHandler<ReadReviewResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("MID", Long.toString(mid));
        params.add("PIndex", Integer.toString(pIndex));
        params.add("PSize", Integer.toString(pSize));

        doRequest("RED2002", params, completionHandler);
    }

    ///查询指定偶得的投票记录
    public void readVotes(long mid, int pIndex, int pSize, InvokeCompletionHandler<ReadVotesResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("MID", Long.toString(mid));
        params.add("PIndex", Integer.toString(pIndex));
        params.add("PSize", Integer.toString(pSize));

        doRequest("RED2003", params, completionHandler);

    }

    ///查询珍藏排行（发现珍藏）
    public void readAlbum(int pIndex, int pSize, InvokeCompletionHandler<ReadAlbumResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("PIndex", Integer.toString(pIndex));
        params.add("PSize", Integer.toString(pSize));

        doRequest("RED4001", params, completionHandler);
    }

    ///查询珍藏中的偶得
    public void readAlbumMottos(long cid, int pIndex, int pSize, InvokeCompletionHandler<ReadMottoResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("CID", Long.toString(cid));
        params.add("PIndex", Integer.toString(pIndex));
        params.add("PSize", Integer.toString(pSize));

        doRequest("RED4002", params, completionHandler);
    }

    ///查询用户排行榜
    public void readUsers(int theMonth, InvokeCompletionHandler<ReadUserRankResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("TheMonth", Integer.toString(theMonth));

        doRequest("RED5001", params, completionHandler);
    }

    ///查询用户详细信息
    public void readUserInfo(String uid, InvokeCompletionHandler<ReadUserResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("UID", uid);

        doRequest("RED5002", params, completionHandler);
    }

    ///查询用户的偶得
    public void readUserMottos(String uid, int pIndex, int pSize, InvokeCompletionHandler<ReadMottoResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("UID", uid);
        params.add("PIndex", Integer.toString(pIndex));
        params.add("PSize", Integer.toString(pSize));

        doRequest("RED5003", params, completionHandler);
    }

    ///读取关注用户的人 RED5004
    public void readUserFollowers(String uid, int pIndex, int pSize, InvokeCompletionHandler<ReadRelatedUserResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("UID", uid);
        params.add("PIndex", Integer.toString(pIndex));
        params.add("PSize", Integer.toString(pSize));

        doRequest("RED5004", params, completionHandler);
    }

    ///读取用户关注的人 RED5005
    public void readUserFollows(String uid, int pIndex, int pSize, InvokeCompletionHandler<ReadRelatedUserResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("UID", uid);
        params.add("PIndex", Integer.toString(pIndex));
        params.add("PSize", Integer.toString(pSize));

        doRequest("RED5005", params, completionHandler);
    }

    ///读取用户的黑名单 RED5006
    public void readUserBans(String uid, int pIndex, int pSize, InvokeCompletionHandler<ReadRelatedUserResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("UID", uid);
        params.add("PIndex", Integer.toString(pIndex));
        params.add("PSize", Integer.toString(pSize));

        doRequest("RED5006", params, completionHandler);
    }

    ///查询当前用户创建的珍藏
    public void readUserAlbum(String uid, long mid, int pIndex, int pSize, InvokeCompletionHandler<ReadAlbumResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("UID", uid);
        params.add("MID", Long.toString(mid));
        params.add("PIndex", Integer.toString(pIndex));
        params.add("PSize", Integer.toString(pSize));

        doRequest("RED5007", params, completionHandler);
    }

    ///查询指定用户喜欢的偶得
    public void readLovedMottos(String uid, int pIndex, int pSize, InvokeCompletionHandler<ReadMottoResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("UID", uid);
        params.add("PIndex", Integer.toString(pIndex));
        params.add("PSize", Integer.toString(pSize));

        doRequest("RED5009", params, completionHandler);
    }

    ///查询指定用户喜欢的珍藏
    public void readLovedAlbums(String uid, int pIndex, int pSize, InvokeCompletionHandler<ReadAlbumResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("UID", uid);
        params.add("PIndex", Integer.toString(pIndex));
        params.add("PSize", Integer.toString(pSize));

        doRequest("RED5010", params, completionHandler);
    }

    ///查询用户的积分记录
    public void readScoreRecord(int pIndex, int pSize, InvokeCompletionHandler<ReadScoreRecordResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("PIndex", Integer.toString(pIndex));
        params.add("PSize", Integer.toString(pSize));

        doRequest("RED5020", params, completionHandler);
    }

    ///查询用户的收支记录
    public void readBillRecord(int pIndex, int pSize, InvokeCompletionHandler<ReadBillRecordResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("PIndex", Integer.toString(pIndex));
        params.add("PSize", Integer.toString(pSize));

        doRequest("RED5021", params, completionHandler);
    }

    ///查询用户的地址簿
    public void readAddresses(InvokeCompletionHandler<ReadAddressResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());


        doRequest("RED5022", params, completionHandler);
    }

    ///读取用户未读消息数
    public void readUserBadge(InvokeCompletionHandler<ReadBadgeResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());


        doRequest("RED5024", params, completionHandler);
    }

    ///读取最近的交谈列表
    public void readRecentTalk(int pIndex, int pSize, InvokeCompletionHandler<ReadRecentTalkResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("PIndex", Integer.toString(pIndex));
        params.add("PSize", Integer.toString(pSize));

        doRequest("RED6001", params, completionHandler);
    }

    ///读取与指定用户的消息记录
    public void readTalkMsgs(String withUid, long start, int take, InvokeCompletionHandler<ReadTalkMsgResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());
        params.add("UID", withUid);
        params.add("Start", Long.toString(start));
        params.add("Take", Integer.toString(take));

        doRequest("RED6002", params, completionHandler);
    }

    ///读取当前用户的提醒通知
    public void readNotices(int pIndex, int pSize, InvokeCompletionHandler<ReadNoticeResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());

        params.add("PIndex", Integer.toString(pIndex));
        params.add("PSize", Integer.toString(pSize));

        doRequest("RED6003", params, completionHandler);
    }

    ///读取可兑换礼品列表
    public void readGifts(int pIndex, int pSize, InvokeCompletionHandler<ReadGiftsResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());

        params.add("PIndex", Integer.toString(pIndex));
        params.add("PSize", Integer.toString(pSize));

        doRequest("RED7001", params, completionHandler);

    }

    ///读取当前用户的礼品兑换记录
    public void readMyExchangeRecord(int pIndex, int pSize, InvokeCompletionHandler<ReadExchangeRecordResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());
        params.add("UserId", ImottoApplication.getInstance().getUserId());
        params.add("Token", ImottoApplication.getInstance().getUserToken());

        params.add("PIndex", Integer.toString(pIndex));
        params.add("PSize", Integer.toString(pSize));

        doRequest("RED7003", params, completionHandler);
    }

    ///读取奖品列表
    public void readAwards(int pIndex, int pSize, InvokeCompletionHandler<ReadAwardsResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());

        params.add("PIndex", Integer.toString(pIndex));
        params.add("PSize", Integer.toString(pSize));

        doRequest("RED8001", params, completionHandler);
    }

    ///读取获奖人列表
    public void readAwardees(int awardId, InvokeCompletionHandler<ReadAwardeeResp> completionHandler){
        RequestParams params = new RequestParams();
        params.add("Sign", ImottoApplication.getInstance().getSign());

        params.add("AwardId", Integer.toString(awardId));

        doRequest("RED8002", params, completionHandler);
    }

    private<T extends ApiResp> void doRequest(String code, RequestParams reqParams, final InvokeCompletionHandler<T> completionHandler){

        Log.i(TAG, "doRequest: "+ code+"; params: "+reqParams.toString());

        client.post(String.format(ApiUrl, code), reqParams, new GsonHttpResponseHandler<T>(completionHandler.getGenericClass()) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, T response) {
                Log.i(TAG, "api invoke success, response is: "+rawJsonResponse);
                completionHandler.onApiCallCompletion(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, T errorResponse) {

                Log.e(TAG, "Api Invoke error:"+ throwable.getMessage());
                try {
                    T result = completionHandler.getGenericClass().newInstance();
                    result.State = -1;
                    result.Msg = "网络不太给力呀，请稍后再试"; //"Error: "+throwable.getMessage();
                    completionHandler.onApiCallCompletion(result);
                }
                catch (Exception e){
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        });
    }

    public interface InvokeCompletionHandler<T>{
        void onApiCallCompletion(T result);

        Class<T> getGenericClass();
    }
}
