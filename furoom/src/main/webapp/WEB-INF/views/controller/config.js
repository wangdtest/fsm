var loanTypes = [
                 {name:"房贷", type:0},
                 {name:"车贷", type:1},
                 {name:"消费贷", type:2},
                 {name:"经营贷", type:3},
                 {name:"抵押贷", type:4},
                 {name:"信用贷", type:5}
                 ];
var amounts = [
               {name:"5万", amount:5},
               {name:"10万", amount:10},
               {name:"20万", amount:20},
               {name:"50万", amount:50},
               {name:"100万", amount:100}
               ];
var periods = [
               {name:"三个月", period:3},
               {name:"六个月", period:6},
               {name:"十二个月", period:12},
               {name:"两年", period:24},
               {name:"三年", period:36},
               {name:"五年", period:60},
               {name:"十年", period:120}
               ];
var provinces = [
                {name:"河北省", code:"001", citys:[{name:"邯郸市", code:"001001"},
                                                {name:"邢台市", code:"001002"},
                                                {name:"保定市", code:"001003"},
                                                {name:"石家庄市", code:"001004"}
                                               ]},
                {name:"山东省", code:"002", citys:[{name:"青岛市", code:"002001"},
                       	                       {name:"济南市", code:"002002"},
                       	                       {name:"梁山市", code:"003003"}
                       	                       ]}
                ];