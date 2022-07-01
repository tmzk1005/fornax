package zk.fornax.common.httpapi;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import zk.fornax.common.utils.IpUtil;

@Data
@NoArgsConstructor
public class IpFilterRuleConf implements Serializable {

    private IpFilterRuleType ipFilterRuleType = IpFilterRuleType.ACCEPT;

    private Set<String> singleIps = new HashSet<>();

    private Set<String> subNets = new HashSet<>();

    private Set<String> ipRanges = new HashSet<>();

    public IpFilterRuleConf(String ips) {
        this(ips, IpFilterRuleType.ACCEPT);
    }

    public IpFilterRuleConf(String ips, IpFilterRuleType ruleType) {
        ipFilterRuleType = ruleType;
        String[] segments = ips.split(";");
        for (String text : segments) {
            text = text.trim();
            if (IpUtil.isIpV4(text)) {
                singleIps.add(text);
            } else if (IpUtil.isIpV4Subnet(text)) {
                subNets.add(text);
            } else if (IpUtil.isIpV4Range(text)) {
                ipRanges.add(text);
            }
        }
    }

    public boolean verify() {
        for (String ip : singleIps) {
            if (!IpUtil.isIpV4(ip)) {
                return false;
            }
        }

        for (String ipSubnet : subNets) {
            if (!IpUtil.isIpV4Subnet(ipSubnet)) {
                return false;
            }
        }

        for (String ipRange : ipRanges) {
            if (!IpUtil.isIpV4Range(ipRange)) {
                return false;
            }
        }
        return true;
    }

}
