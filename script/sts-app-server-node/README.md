# A simple app server using STS

App Server一般作为一个企业的应用服务器，它管理着OSS的
AccessKeyId/AccessKeySecret，服务于众多的客户端。当客户端（移动端
/Web/Client等）需要访问OSS时，它们向App Server请求一个临时的token，并
利用这个token从OSS下载或者向OSS上传文件。

App Server可以实现更复杂的策略，为不同的客户端提供不同权限的token，隔
离不同的客户端的存储路径等。

使用参考：https://help.aliyun.com/document_detail/oss/practice/ram_guide_dir/no_user_accout.html

## Run

### Checkout code

    git clone https://github.com/rockuw/node-sts-app-server.git
    cd node-sts-app-server

### Install dependencies

    npm install

### Start server

    node index.js

### Open in your browser

http://localhost:3000/
