package com.jjt.common.api;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjt.common.api.entity.CustomAccount;
import com.jjt.common.api.enums.ResultType;
import com.jjt.common.api.response.BaseResponse;
import com.jjt.common.api.response.GetCustomAccountsResponse;
import com.jjt.common.api.response.GetCustomOnlineListResponse;
import com.jjt.common.api.response.GetWaitCaseListResponse;
import com.jjt.common.entity.message.resp.Article;
import com.jjt.common.entity.message.resp.BaseMsg;
import com.jjt.common.entity.message.resp.ImageMsg;
import com.jjt.common.entity.message.resp.MpNewsMsg;
import com.jjt.common.entity.message.resp.MusicMsg;
import com.jjt.common.entity.message.resp.NewsMsg;
import com.jjt.common.entity.message.resp.TextMsg;
import com.jjt.common.entity.message.resp.VideoMsg;
import com.jjt.common.entity.message.resp.VoiceMsg;
import com.jjt.common.exception.WeixinException;
import com.jjt.common.utils.BeanUtils;
import com.jjt.common.utils.JSONUtil;
import com.jjt.common.utils.StrUtil;

/**
 * 客服相关API
 *
 */
public class CustomAPI extends BaseAPI {

    private static final Logger LOG = LoggerFactory.getLogger(CustomAPI.class);

//    public CustomAPI(ApiConfig config) {
//        super(config);
//    }
    public CustomAPI(String accessToken){
    	super(accessToken);
    }

	/**
	 * 发布客服文本消息
	 * @param openid
	 * @param message
	 * @return
	 */
	public ResultType sendTextCustomMessage(String openid, BaseMsg message) {
		BeanUtils.requireNonNull(openid, "openid is null");
		BeanUtils.requireNonNull(message, "message is null");
		LOG.debug("发布客服消息......");
		String url = BASE_API_URL + "cgi-bin/message/custom/send?access_token=#";
		TextMsg msg = (TextMsg) message;
//		final Map<String, Object> params = new HashMap<String, Object>();
//		params.put("touser", openid);
		StringBuffer sb = new StringBuffer();
		sb.append("{\"touser\"").append(":");
		sb.append("\"").append(openid).append("\"").append(",");
		sb.append("\"text\":").append("{\"content\":");
		sb.append("\"");
		sb.append(msg.getContent());
		sb.append("\"},").append("\"msgtype\":\"text\"}");
		BaseResponse response = executePost(url, sb.toString());
		if(!ResultType.SUCCESS.getCode().toString().equals(response.getErrcode())){
			LOG.info("param is " + sb.toString());
			LOG.info("Send text custom message errorcode is " + response.getErrcode() + "errorMsg is " + response.getErrmsg());
		}
		return ResultType.get(response.getErrcode());

	}
    /**
     * 发布客服消息
     *
     * @param openid  关注者ID
     * @param message 消息对象，支持各种消息类型
     * @return 调用结果
     */
    public ResultType sendCustomMessage(String openid, BaseMsg message) {
        BeanUtils.requireNonNull(openid, "openid is null");
        BeanUtils.requireNonNull(message, "message is null");
        LOG.debug("发布客服消息......");
        String url = BASE_API_URL + "cgi-bin/message/custom/send?access_token=#";
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("touser", openid);
        if (message instanceof TextMsg) {
            TextMsg msg = (TextMsg) message;
            params.put("msgtype", "text");
            Map<String, String> text = new HashMap<String, String>();
            text.put("content", msg.getContent());
            params.put("text", text);
        } else if (message instanceof ImageMsg) {
            ImageMsg msg = (ImageMsg) message;
            params.put("msgtype", "image");
            Map<String, String> image = new HashMap<String, String>();
            image.put("media_id", msg.getMediaId());
            params.put("image", image);
        } else if (message instanceof VoiceMsg) {
            VoiceMsg msg = (VoiceMsg) message;
            params.put("msgtype", "voice");
            Map<String, String> voice = new HashMap<String, String>();
            voice.put("media_id", msg.getMediaId());
            params.put("voice", voice);
        } else if (message instanceof VideoMsg) {
            VideoMsg msg = (VideoMsg) message;
            params.put("msgtype", "video");
            Map<String, String> video = new HashMap<String, String>();
            video.put("media_id", msg.getMediaId());
            video.put("thumb_media_id", msg.getMediaId());
            video.put("title", msg.getTitle());
            video.put("description", msg.getDescription());
            params.put("video", video);
        } else if (message instanceof MusicMsg) {
            MusicMsg msg = (MusicMsg) message;
            params.put("msgtype", "music");
            Map<String, String> music = new HashMap<String, String>();
            music.put("thumb_media_id", msg.getThumbMediaId());
            music.put("title", msg.getTitle());
            music.put("description", msg.getDescription());
            music.put("musicurl", msg.getMusicUrl());
            music.put("hqmusicurl", msg.getHqMusicUrl());
            params.put("music", music);
        } else if (message instanceof NewsMsg) {
            NewsMsg msg = (NewsMsg) message;
            params.put("msgtype", "news");
            Map<String, Object> news = new HashMap<String, Object>();
            List<Object> articles = new ArrayList<Object>();
            List<Article> list = msg.getArticles();
            for (Article article : list) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("title", article.getTitle());
                map.put("description", article.getDescription());
                map.put("url", article.getUrl());
                map.put("picurl", article.getPicUrl());
                articles.add(map);
            }
            news.put("articles", articles);
            params.put("news", news);
        } else if (message instanceof MpNewsMsg) {
        	MpNewsMsg msg = (MpNewsMsg) message;
            params.put("msgtype", "mpnews");
            Map<String, String> news = new HashMap<String, String>();
            news.put("media_id", msg.getMediaId());
            params.put("mpnews", news);
        }
        BaseResponse response = executePost(url, JSONUtil.toJson(params));
		if(!ResultType.SUCCESS.getCode().toString().equals(response.getErrcode())){
			LOG.info("param is " + JSONUtil.toJson(params));
			LOG.info("Send custom message errorcode is " + response.getErrcode() + "errorMsg is " + response.getErrmsg());
		}
        return ResultType.get(response.getErrcode());
    }

    /**
     * 添加客服帐号
     *
     * @param customAccount 客服对象
     * @return 添加结果
     */
    public ResultType addCustomAccount(CustomAccount customAccount) {
        LOG.debug("添加客服帐号.....");
        BeanUtils.requireNonNull(customAccount.getAccountName(), "帐号必填");
        BeanUtils.requireNonNull(customAccount.getNickName(), "昵称必填");
        String url = BASE_API_URL + "customservice/kfaccount/add?access_token=#";
        Map<String, String> params = new HashMap<String, String>();
        params.put("kf_account", customAccount.getAccountName());
        params.put("nickname", customAccount.getNickName());
        if (StrUtil.isNotBlank(customAccount.getPassword())) {
            params.put("password", customAccount.getPassword());
        }
        BaseResponse response = executePost(url, JSONUtil.toJson(params));
        return ResultType.get(response.getErrcode());
    }

    /**
     * 修改客服帐号信息
     *
     * @param customAccount 客服帐号信息
     * @return 修改结果
     */
    public ResultType updateCustomAccount(CustomAccount customAccount) {
        LOG.debug("修改客服帐号信息......");
        BeanUtils.requireNonNull(customAccount.getAccountName(), "帐号必填");
        BeanUtils.requireNonNull(customAccount.getNickName(), "昵称必填");
        String url = BASE_API_URL + "customservice/kfaccount/update?access_token=#";
        Map<String, String> params = new HashMap<String, String>();
        params.put("kf_account", customAccount.getAccountName());
        params.put("nickname", customAccount.getNickName());
        if (StrUtil.isNotBlank(customAccount.getPassword())) {
            params.put("password", customAccount.getPassword());
        }
        BaseResponse response = executePost(url, JSONUtil.toJson(params));
        return ResultType.get(response.getErrcode());
    }

    /**
     * 删除客服帐号
     * @param accountName 客服帐号名
     * @return 删除结果
     */
    public ResultType deleteCustomAccount(String accountName) {
        LOG.debug("删除客服帐号信息......");
        String url = BASE_API_URL + "customservice/kfaccount/del?access_token=#&kf_account=" + accountName;
        BaseResponse response = executePost(url, null);
        return ResultType.get(response.getErrcode());
    }

    /**
     * 设置客服帐号头像
     *
     * @param accountName 客服帐号名
     * @param file        头像文件
     * @return 设置结果
     */
    public ResultType uploadHeadImg(String accountName, File file) {
        LOG.debug("设置客服帐号头像.....");
        BeanUtils.requireNonNull(accountName, "帐号必填");
        BeanUtils.requireNonNull(file, "文件为空");

        String fileName = file.getName().toLowerCase();
        if (!fileName.endsWith("jpg")) {
            throw new WeixinException("头像必须是jpg格式");
        }
        String url = BASE_API_URL + "customservice/kfaccount/uploadheadimg?access_token=#&kf_account=" + accountName;
        BaseResponse response = executePost(url, null, file);
        return ResultType.get(response.getErrcode());
    }

    /**
     * 获取所有客服帐号信息
     * @return 所有客服帐号信息对象
     */
    public GetCustomAccountsResponse getCustomAccountList() {
        LOG.debug("获取所有客服帐号信息....");
        GetCustomAccountsResponse response;
        String url = BASE_API_URL + "cgi-bin/customservice/getkflist?access_token=#";
        BaseResponse r = executeGet(url);
        String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();
        response = JSONUtil.toBean(resultJson, GetCustomAccountsResponse.class);
        return response;
    }
    
    /**
     * 获取所有客服帐号信息
     * @return 所有客服帐号信息对象
     */
    public GetCustomOnlineListResponse getCustomOnlineList() {
        LOG.debug("获取在线客服接待信息....");
        GetCustomOnlineListResponse response;
        String url = BASE_API_URL + "cgi-bin/customservice/getonlinekflist?access_token=#";
        BaseResponse r = executeGet(url);
        String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();
        response = JSONUtil.toBean(resultJson, GetCustomOnlineListResponse.class);
        return response;
    }
    
    /**
     * 获取所有客服帐号信息
     * @return 所有客服帐号信息对象
     */
    public GetWaitCaseListResponse GetWaitCaseList() {
        LOG.debug("获取在线客服接待信息....");
        GetWaitCaseListResponse response;
        String url = BASE_API_URL + "customservice/kfsession/getwaitcase?access_token=#";
        BaseResponse r = executeGet(url);
        String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();
        response = JSONUtil.toBean(resultJson, GetWaitCaseListResponse.class);
        return response;
    }
}
