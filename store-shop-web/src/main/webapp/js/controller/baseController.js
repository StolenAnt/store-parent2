app.controller('baseController',function ($scope) {
    //分页控制
    $scope.paginationConf={
        currentPage: 1, //当前页
        totalItems: 10,//总记录数
        itemsPerPage: 10,//每页记录数
        perPageOptions: [5,10, 20, 30, 40, 50],//分页选项
        onChange:function () {  //当页码变更的时候触发事件
            $scope.reloadList();
        }

    };

    $scope.reloadList=function(){
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    }

    $scope.selectIds=[];

    $scope.updateSelection=function ($event,id) {
        if($event.target.checked){
            $scope.selectIds.push(id);
        }else{
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index,1);
        }
    }

    $scope.jsonToString=function (jsonString,key) {
        var json=JSON.parse(jsonString);
        var value="";
        for (var i=0;i<json.length;i++){
            if (i>0){
                value+=" , ";
            }
            value+=json[i][key];
        }
        return value;
    }

    //在list集合中根据某Key的值查询对象
    $scope.searchObjectByKey=function (list,key,keyValue) {
        for (var i=0;i<list.length;i++){
           if(list[i][key]==keyValue){
               return list[i];
           }
        }
        return null;
    }

});