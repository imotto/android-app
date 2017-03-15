package net.imotto.imottoapp.services.models;

/**
 * Created by sunht on 16/10/27.
 *
 */

public class RegisterDeviceResp extends ApiResp {
    public String Sign;

    public String UpdateFlag; //	版本升级标识	不需升级：0，需要升级：1，强制升级：2
    public String NewVersion; //	新版本号	新版本号
    public String UpdateSummary; //	更新说明	更新说明
    public String DownloadUrl;	//下载地址	下载地址

    // 附加消息
    public SpotlightModel Extra;

}
