package zk.fornax.manager.bean.vo;

import lombok.Getter;
import lombok.Setter;

import zk.fornax.manager.bean.Role;
import zk.fornax.manager.bean.po.User;

@Getter
@Setter
public class UserVo extends BaseAuditableVo<User> {

    private String id;

    private String username;

    private String nickname;

    private boolean enabled = true;

    private String email;

    private String phone;

    private Role role = Role.NORMAL_USER;

    @SuppressWarnings("unchecked")
    @Override
    public UserVo initFromPo(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.enabled = user.isEnabled();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.role = user.getRole();
        copyAuditInfo(user);
        return this;
    }

    public static UserVo fromPo(User user) {
        return new UserVo().initFromPo(user);
    }

}
