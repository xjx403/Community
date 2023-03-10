package com.coder.community.controller;

import com.coder.community.annotation.LoginRequired;
import com.coder.community.dao.UserMapper;
import com.coder.community.entity.User;
import com.coder.community.service.FollowService;
import com.coder.community.service.LikeService;
import com.coder.community.service.UserService;
import com.coder.community.util.CommunityConstant;
import com.coder.community.util.CommunityUtil;
import com.coder.community.util.HostHolder;
import com.sun.deploy.net.HttpResponse;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author 不想想名字
 */
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    @Value("${community.path.upload}")
    private String upLoadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @RequestMapping(value = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("error","文件上传失败");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式错误");
            return "/site/setting";
        }

        //生成随机的文件名
        fileName = CommunityUtil.generateUUD() + suffix;
        //确定文件存放的路径
        File dest = new File(upLoadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            LOGGER.error("上传文件失败" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器异常！",e);
        }

        //更新当前用户的头像的路径
        //http://localhost:8080/community/user/header/XXX.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId() , headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}" , method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName , HttpServletResponse response){
        //服务存放路径
        fileName = upLoadPath + "/" + fileName;
        //获取文件类型
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        //响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
                )
        {
            byte[] buffer =new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            LOGGER.error("读取头像失败！" + e.getMessage());
        }
    }

    /**更新密码
     *
     * @return
     */
    @RequestMapping(path = "/update/password" , method = RequestMethod.GET)
    public String updatePassword(String oldPassword , String newPassword ,String sure, Model model){
        if(!sure.equals(newPassword)){
            model.addAttribute("sureError","两次输入不相同");
            return "/site/setting";
        }
        if(newPassword == null){
            model.addAttribute("newPasswordError","新密码不能为空");
            return "/site/setting";
        }
        if(newPassword.equals(oldPassword)){
            model.addAttribute("newPasswordError","新密码和旧密码相同！！");
            return "/site/setting";
        }
        User user = hostHolder.getUser();
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if(!oldPassword.equals(user.getPassword())){
            model.addAttribute("oldPasswordError","密码错误！");
            return "/site/setting";
        }
        userService.updatePassword(user.getId() , CommunityUtil.md5(newPassword + user.getSalt()));
        return "redirect:/index";
    }

    //个人主页
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw  new RuntimeException("该用户不存在");
        }

        //用户的基本信息
        model.addAttribute("user",user);
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER,userId);
        model.addAttribute("followerCount",followerCount);

        //是否已关注
        boolean hasFollowed = false;
        if(hostHolder.getUser() != null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);
        return "/site/profile";
    }
}
