'use strict';
/**
 * 权限控制组件
 * 将当前element上需要的权限与用户的权限进行比较， 如果没有访问权限则隐藏
 * @param  {[SessionSrv]} SessionSrv [Session服务]
 * @return {[angular directive]}            [directive]
 */
function permissions(SessionSrv) {
    'ngInject';

    /**
     * [_link description]
     * @param  {[type]} scope [description]
     * @param  {[type]} elem  [description]
     * @param  {[type]} attrs [permissions: 访问权限]
     * @return {[type]}       [description]
     */
	function _link(scope, elem, attrs) {
		let elePermissions = attrs.permissions;
		let currentUser = SessionSrv.getCurrentUser();
		let userPermissions = [];
		if (currentUser) {
			userPermissions = currentUser.permissions || [];
		}
		let hasPermissions = (userPermissions.indexOf(elePermissions) >= 0);
		if (!hasPermissions) {
			elem.remove();
		}		
	}

    let directive = {
        restrict: 'A',
        link: _link
    };
    return directive;
}

module.exports = {
    name: 'permissions',
    fn: permissions
};
