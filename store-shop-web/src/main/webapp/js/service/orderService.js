app.service('orderService',function($http){

    this.findAll=function () {
        return $http.get('../order/findAll.do');
    }

    this.updeteStatus=function (id,status) {
        return $http.get('../order/updeteStatus.do?id='+id+'&status='+status);
    }
});