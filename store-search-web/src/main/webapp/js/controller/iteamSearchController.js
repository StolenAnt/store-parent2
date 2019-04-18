app.controller('searchController',function ($location,$scope,searchService) {





    //定义搜索对象的结构
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sort':''
        ,'sortField':''};


    $scope.search=function () {
        $scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(
            function (response) {
            $scope.resultMap=response;

                //构建分页标签
                buildPageLabel();

        });
    }


    //构建分页标签
     buildPageLabel=function(){
        $scope.pageLabel=[];
        var firstPage=1;//起始页码
        var lastPage=$scope.resultMap.totalPage;//终止页码
        $scope.firstDot=true;
         $scope.lastDot=true;

         if ($scope.resultMap.totalPage>5){
             if ($scope.searchMap.pageNo<=3){
                 lastPage=5;
                 $scope.firstDot=false;
             } else if($scope.searchMap.pageNo>=$scope.resultMap.totalPage-2){
                 firstPage=$scope.resultMap.totalPage-4;
                 $scope.lastDot=false;
             }else{
                 firstPage=$scope.searchMap.pageNo-2;
                 lastPage=$scope.searchMap.pageNo+2;
             }
         }else{
             $scope.firstDot=false;
             $scope.lastDot=false;
         }

        for (var i=firstPage;i<=lastPage;i++){
            $scope.pageLabel.push(i);
        }

    }
    //添加搜索项 就是改变SearchMap的值
    $scope.addSearchItem=function (key,value) {
        if (key=='category'||key=='brand'||key=='price'){//点击的是分类或品牌
            $scope.searchMap[key]=value;

        }else{ //用户点击的是规格
            $scope.searchMap.spec[key]=value;
        }
        $scope.searchMap.pageNo=1;
        $scope.search();
    }

    //撤销搜索项目
    $scope.removeSearchItem=function (key) {
        if (key=='category'||key=='brand'||key=='price'){//点击的是分类或品牌
            $scope.searchMap[key]="";

        }else{ //用户点击的是规格
            delete $scope.searchMap.spec[key];
        }
        $scope.searchMap.pageNo=1;
        $scope.search();
    }

    //分页查询
    $scope.queryByPage=function (pageNo) {
        if (pageNo<1||pageNo>$scope.resultMap.totalPage){
            return ;
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    }

    //控制省略号
    $scope.isTopPage=function () {
        if ($scope.searchMap.pageNo==1){
            return true;
        } else{
            return false;
        }
    }
    $scope.isEndPage=function () {
        if ($scope.searchMap.pageNo==$scope.resultMap.totalPage){
            return true;
        } else{
            return false;
        }
    }


    //排序查询
    $scope.sortSearch=function (sortField,sort) {

        $scope.searchMap.sort=sort;
        $scope.searchMap.sortField=sortField;
        $scope.search();//查询
    }

    //关键字判断是否是品牌
    $scope.keywordsIsBrand=function () {

        for(var i=0;i<$scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    }

    //首页加载关键字
    $scope.loadkeywords=function () {
       $scope.searchMap.keywords= $location.search()['keywords'];
       $scope.search();
    }
});