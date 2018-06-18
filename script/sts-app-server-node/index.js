var express = require('express');
var STS = require('ali-oss').STS;
var co = require('co');
var fs = require('fs');
var app = express();

app.get('/', function (req, res) {
  var conf = JSON.parse(fs.readFileSync('./config.json'));
  var policy;
  if (conf.PolicyFile) {
    policy = fs.readFileSync(conf.PolicyFile).toString('utf-8');
  }

  var client = new STS({
    accessKeyId: conf.AccessKeyId,
    accessKeySecret: conf.AccessKeySecret,
  });

  co(function* () {
    var result = yield client.assumeRole(conf.RoleArn, policy, conf.TokenExpireTime);
    console.log(result);
    
    res.set('Access-Control-Allow-Origin', '*');
    res.set('Access-Control-Allow-METHOD', 'GET');
    res.json({
      StatusCode: 200,
      AccessKeyId: result.credentials.AccessKeyId,
      AccessKeySecret: result.credentials.AccessKeySecret,
      SecurityToken: result.credentials.SecurityToken,
      Expiration: result.credentials.Expiration
    });
  }).then(function () {
    // pass
  }).catch(function (err) {
    res.json({
        StatusCode: 500,
        ErrorCode: err.code,
        ErrorMessage: err.message
    });
  });
});

app.listen(9000, function () {
  console.log('App started.');
});
