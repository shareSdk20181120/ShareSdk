using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using cn.sharesdk.unity3d;
public class ShareManager : MonoBehaviour {
    private ShareSDK ssdk;
    // Use this for initialization
    void Start () {
        ShareContent content = new ShareContent();
        content.SetText("this is a test string.");
        content.SetImageUrl("https://f1.webshare.mob.com/code/demo/img/1.jpg");
        content.SetTitle("test title");
        content.SetShareType(ContentType.Image);

        ssdk.shareHandler = ShareResultHandler;

        //Share by the menu
        ssdk.ShowPlatformList(null, content, 100, 100);
        /*
                //share by the content editor
                ssdk.ShowShareContentEditor(PlatformType.SinaWeibo, content);

                //share directly
                ssdk.ShareContent(PlatformType.SinaWeibo, content);
                */

        ssdk.authHandler = AuthResultHandler;

        ssdk.Authorize(PlatformType.SinaWeibo);

        ssdk.showUserHandler = GetUserInfoResultHandler;

        ssdk.GetUserInfo(PlatformType.SinaWeibo);
    }

    void ShareResultHandler(int reqID, ResponseState state, PlatformType type, Hashtable result)
    {
        if (state == ResponseState.Success)
        {
            print("share result :");
            print(MiniJSON.jsonEncode(result));
        }
        else if (state == ResponseState.Fail)
        {
            print("fail! throwable stack = " + result["stack"] + "; error msg = " + result["msg"]);
        }
        else if (state == ResponseState.Cancel)
        {
            print("cancel !");
        }
    }

    void AuthResultHandler(int reqID, ResponseState state, PlatformType type, Hashtable result)
    {
        if (state == ResponseState.Success)
        {
            print("authorize success !");
        }
        else if (state == ResponseState.Fail)
        {
            print("fail! error code = " + result["error_code"] + "; error msg = " + result["error_msg"]);
        }
        else if (state == ResponseState.Cancel)
        {
            print("cancel !");
        }
    }

    void GetUserInfoResultHandler(int reqID, ResponseState state, PlatformType type, Hashtable result)
    {
        if (state == ResponseState.Success)
        {
            print("get user info result :");
            print(MiniJSON.jsonEncode(result));
        }
        else if (state == ResponseState.Fail)
        {
            print("fail! error code = " + result["error_code"] + "; error msg = " + result["error_msg"]);
        }
        else if (state == ResponseState.Cancel)
        {
            print("cancel !");
        }
    }
}
