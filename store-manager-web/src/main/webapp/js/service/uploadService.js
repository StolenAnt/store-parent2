app.service('uploadService',function ($http) {

    this.uploadFile=function () {
        var formadta=new FormData();
        formadta.append('file',file.files[0]);//文件上传框的name只能是file了
        return $http({
            url:'../upload.do',
            method:'post',
            data:formadta,
            headers:{'Content-Type':undefined},
            transformRequest:angular.identity
        });
    }

});