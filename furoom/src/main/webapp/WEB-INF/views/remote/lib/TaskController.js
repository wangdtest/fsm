if (!FuRoomClient) {
	throw "需要EasyService平台远程服务调用JS客户端支持";
}

var TaskApp = angular.module("taskApp", []);

var jobService = FuRoomClient.getRemoteProxy("http://123.57.6.110/furoom/gpps.service.IHelpService");

TaskApp.controller('TaskCtrl', function($scope){
	$scope.total=12;
	$scope.refresh = function(){
		
	}
	
	jobService.findPublicHelps(-1,0,5, function(rt, success, method, id){
		$scope.total = rt.get('total');
		alert(rt.get('total'));
		$scope.$apply();
	});
});
