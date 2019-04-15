app.controller('contentController',function ($scope,contentService) {


    $scope.contentList=[];
    $scope. findfindByCategoryId=function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                $scope.contentList[categoryId]=response;
            }
        );
    }
});