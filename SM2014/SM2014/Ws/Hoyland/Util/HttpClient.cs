﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Configuration;
using System.Windows.Forms;

namespace Ws.Hoyland.Util
{
    /**/
    /// <summary>
    /// 支持 Session 和 Cookie 的 WebClient。
    /// </summary>
    public class HttpClient : WebClient
    {
        // Cookie 容器
        private CookieContainer cookieContainer;
        private Configuration cfa = null;

        /**/
        /// <summary>
        /// 创建一个新的 WebClient 实例。
        /// </summary>
        public HttpClient()
        {
            this.cookieContainer = new CookieContainer();
            
            cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);
            if (cfa == null)
            {
                MessageBox.Show("加载配置文件失败!");
            }
        }

        /**/
        /// <summary>
        /// 创建一个新的 WebClient 实例。
        /// </summary>
        /// <param name="cookie">Cookie 容器</param>
        public HttpClient(CookieContainer cookies)
        {
            this.cookieContainer = cookies;
        }

        /**/
        /// <summary>
        /// Cookie 容器
        /// </summary>
        public CookieContainer Cookies
        {
            get { return this.cookieContainer; }
            set { this.cookieContainer = value; }
        }

        /**/
        /// <summary>
        /// 返回带有 Cookie 的 HttpWebRequest。
        /// </summary>
        /// <param name="address"></param>
        /// <returns></returns>
        protected override WebRequest GetWebRequest(Uri address)
        {
            WebRequest request = base.GetWebRequest(address);
            if (request is HttpWebRequest)
            {
                HttpWebRequest httpRequest = request as HttpWebRequest;
                httpRequest.CookieContainer = cookieContainer;

                ConfigurationManager.RefreshSection("appSettings");

                //httpRequest.Timeout = 1000 * 2;
                //httpRequest.ReadWriteTimeout = 1000 * 2;

                httpRequest.Timeout = 1000 * Int32.Parse(cfa.AppSettings.Settings["TIMEOUT"].Value);
                httpRequest.ReadWriteTimeout = 1000 * Int32.Parse(cfa.AppSettings.Settings["TIMEOUT"].Value);
            }
            return request;
        }

        protected override WebResponse GetWebResponse(WebRequest request)
        {
            WebResponse r = null;

            try
            {
                r = base.GetWebResponse(request);
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
                //throw new Exception("XXX");
            }

            if (r is HttpWebResponse)

                this.cookieContainer.Add((r as HttpWebResponse).Cookies);

            return r;

        }
    }
}
