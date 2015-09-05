if (!FuRoomClient) {
	throw "需要EasyService平台远程服务调用JS客户端支持";
}
if (!ejs) {
	throw "需要EasyService平台模板支持";
}  

var userService = FuRoomClient.getRemoteProxy('/furoom/fsm.service.IUserService/invoke');
var operationService = FuRoomClient.getRemoteProxy('/furoom/fsm.service.IOperationService/invoke');
var productService = FuRoomClient.getRemoteProxy('/furoom/fsm.service.IProductService/invoke');

var queryOperationsByState=function(user, state){
	var res = operationService.findAllOperationByUserAndStateAndType(user.id, state, -1,0,100);
	var ds = res.get("result");
	var datasArray = new Array();
	if(ds!=null){
	for(var i=0; i<ds.size(); i++){
		var d = ds.get(i);
		datasArray.push( {type:d.loanType, amount:d.amount, period:d.period, applytime:d.createtime, state:d.state});
	}
	}
	return datasArray;
}


var MyAccountApp = angular.module("MyAccountApp", [])
.controller('myaccountCtrl', ['$scope', function ($scope) {
	$scope.user = userService.getCurrentUser();
	if($scope.user==null){
		alert("error user is null!");
		$location.path('/error');
	}
	var waitAudit = function(){
		$scope.datas = queryOperationsByState($scope.user,0);
		$scope.total = $scope.datas.length;
	}
	var handling = function(){
		$scope.datas = queryOperationsByState($scope.user,3);
		$scope.total = $scope.datas.length;
	}
	var done = function(){
		$scope.datas = queryOperationsByState($scope.user,4);
		$scope.total = $scope.datas.length;
	}
	var refuse = function(){
		$scope.datas = queryOperationsByState($scope.user,2);
		$scope.total = $scope.datas.length;
	}
	
	
    $scope.tabs = [{
    	title: '被拒绝贷款',
    	state:1,
    	url: 'template/user/refuse.html',
        handler : refuse
    }, {
        title: '已完成贷款',
        state:2,
        url: 'template/user/done.html',
        handler : done
    }, {
        title: '处理中贷款',
        state:3,
        url: 'template/user/handling.html',
        handler : handling
    },{
        title: '待审核贷款',
        state: 4,
        url: 'template/user/waitAudit.html',
        handler : waitAudit
    }
    ];
$scope.datas = null;

$scope.tabs[3].handler();
$scope.currentTab = $scope.tabs[3];


$scope.onClickTab = function (tab) {
	tab.handler();
    $scope.currentTab = tab;
}
$scope.isActiveTab = function(tabUrl) {
    return tabUrl == $scope.currentTab.url;
}

}]);