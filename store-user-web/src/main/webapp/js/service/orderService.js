app.service('orderService',function($http){

    this.findOrderListByUser=function () {
        return $http.get('../order/findOrderListByUser.do');
    }
});