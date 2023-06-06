public class CyberWrapper {
//com.jflyfox.modules.filemanager.FileManagerController.index()

    public void index() {
        HttpServletRequest request = getRequest();

        try {
            request.setCharacterEncoding("UTF-8");
            getResponse().setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        FileManager fm = new FileManager(getRequest());

        JSONObject responseData = null;

        String mode = "";
        String path = "";
        boolean needPath = false;
        boolean putTextarea = false;
        if (!auth()) {
            fm.error(fm.lang("AUTHORIZATION_REQUIRED"));
        } else {
            String contextPath = request.getContextPath();
            // 设置contextPath
            fm.setGetVar("contextPath", contextPath);

            mode = request.getParameter("mode");
            path = request.getParameter("path");

            if (path != null) {
                try {
                    if (request.getMethod().equals("GET"))
                        path = new String(path.getBytes("ISO8859-1"), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                needPath = fm.setGetVar("path", path);
            }

            if (request.getMethod().equals("GET")) {
                if (mode != null && mode != "") {
                    if (mode.equals("getinfo")) {
                        if (needPath) {
                            responseData = fm.getInfo();
                        }
                    } else if (mode.equals("getfolder")) {
                        if (needPath) {
                            responseData = fm.getFolder();
                        }
                    } else if (mode.equals("rename")) {
                        String oldFile = request.getParameter("old");
                        String newFile = request.getParameter("new");
                        try {
                            oldFile = new String(oldFile.getBytes("ISO8859-1"), "UTF-8");
                            newFile = new String(newFile.getBytes("ISO8859-1"), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        if (fm.setGetVar("old", oldFile) && fm.setGetVar("new", newFile)) {
                            responseData = fm.rename();
                        }
                    } else if (mode.equals("delete")) {
                        if (needPath) {
                            responseData = fm.delete();
                        }
                    } else if (mode.equals("addfolder")) {
                        String name = request.getParameter("name");
                        try {
                            name = new String(name.getBytes("ISO8859-1"), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        if (needPath && fm.setGetVar("name", name)) {
                            responseData = fm.addFolder();
                        }
                    } else if (mode.equals("download")) {
                        if (needPath) {
                            fm.download(getResponse());
                        }
                    } else if (mode.equals("preview")) {
                        if (needPath) {
                            fm.preview(getResponse());
                        }
                    } else if (mode.equals("editfile")) {
                        if (needPath) {
                            responseData = fm.editFile();
                        }
                    } else {
                        fm.error(fm.lang("MODE_ERROR"));
                    }
                }
            } else if (request.getMethod().equals("POST")) {

                if (mode == null) {
                    mode = "upload";
                    responseData = fm.add();
                    putTextarea = true;
                } else if (mode.equals("savefile")) {
                    if (needPath && fm.setGetContent("content", request.getParameter("content"))) {
                        responseData = fm.saveFile();
                    }
                }

            }
        }
        if (responseData == null) {
            responseData = fm.getError();
        }
        if (responseData != null) {
            String responseStr = responseData.toString();
            if (putTextarea)
                responseStr = "<textarea>" + responseStr + "</textarea>";
            log.info("mode:" + mode + ",response:" + responseStr);
            renderText(responseStr);
        } else {
            renderNull();
        }

    }

//com.jflyfox.modules.filemanager.FileManager.download(javax.servlet.http.HttpServletResponse $ARG0)

    public void download(HttpServletResponse resp) {
        File file = new File(getRealFilePath());
        if (this.get.get("path") != null && file.exists()) {
            resp.setHeader("Content-type", "application/force-download");
            resp.setHeader("Content-Disposition", "inline;filename=\"" + fileRoot + this.get.get("path") + "\"");
            resp.setHeader("Content-Transfer-Encoding", "Binary");
            resp.setHeader("Content-length", "" + file.length());
            resp.setHeader("Content-Type", "application/octet-stream");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            readFile(resp, file);
        } else {
            this.error(sprintf(lang("FILE_DOES_NOT_EXIST"), this.get.get("path")));
        }
    }

//com.jflyfox.modules.filemanager.FileManager.readFile(javax.servlet.http.HttpServletResponse $ARG0, java.io.File $ARG1)

    private void readFile(HttpServletResponse resp, File file) {
        OutputStream os = null;
        FileInputStream fis = null;
        try {
            os = resp.getOutputStream();
            fis = new FileInputStream(file);
            byte fileContent[] = new byte[(int) file.length()];
            fis.read(fileContent);
            os.write(fileContent);
        } catch (Exception e) {
            this.error(sprintf(lang("INVALID_DIRECTORY_OR_FILE"), file.getName()));
        } finally {
            try {
                if (os != null)
                    os.close();
            } catch (Exception e2) {
            }
            try {
                if (fis != null)
                    fis.close();
            } catch (Exception e2) {
            }
        }
    }

}
