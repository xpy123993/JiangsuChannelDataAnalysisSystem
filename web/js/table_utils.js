/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function get_time_name(index){
    if(index < 3) return "月度";
    if(index < 6) return "季度";
    return "年度";
}

function fillData_Type_0(index, parameters){
    return [
            {
                field: "ADDR1",
                title: "地区"
                
            },
            {
                field: "ADDR2",
                title: "单位"
            },
            {
                field: "PORTNAME",
                title: "闸号"
            },
            {
                field: "CSCHEDULEDATE",
                title: get_time_name(index)
            },
            {
                field: "OPENINGTIMES",
                title: "开放次数",
                formatter:function(value, row, index){
                   if(value === '0') return value;
                   return '<a href="#" onclick="onItemClick(' + index + ')">' + value + '</a>';
               }
            },
            {
                field: "OPENINGDAYS",
                title: "开放天数"
            }
        ];
}

function fillData_Type_1(index, parameters){
    return [
            {
                field: "ADDR1",
                title: "地区"
            },
            {
                field: "ADDR2",
                title: "单位"
            },
            {
                field: "PORTNAME",
                title: "闸号"
            },
            {
                field: "CSCHEDULEDATE",
                title: get_time_name(index)
            },
            {
                field: "SHIPCOUNT",
                title: "船只数量"
            },
            {
                field: "SHIPWEIGHT",
                title: "船只总载重"
            }
        ];
     
}

function fillData_Type_2(index, parameters){
    return [
            {
                field: "ADDR1",
                title: "地区"
            },
            {
                field: "ADDR2",
                title: "单位"
            },
            {
                field: "PORTNAME",
                title: "闸号"
            },
            {
                field: "CSCHEDULEDATE",
                title: get_time_name(index)
            },
            {
                field: "DIRECTION",
                title: "上下行标志",
                formatter:function(value, row, index){
                   return '<a href="#" onclick="onItemClick(' + index + ')">' + value + '</a>';
               }
            },
            {
                field: "AVGS",
                title: "平均过闸时间"
            }
        ];
}

function fill_data(type, parameters){
    
    var cols;
    if(type % 3 === 0)
        cols = fillData_Type_0(type, parameters);
    if(type % 3 === 1)
        cols = fillData_Type_1(type, parameters);
    if(type % 3 === 2)
        cols = fillData_Type_2(type, parameters);
    
    return {
        method: "POST",
        contentType: "application/x-www-form-urlencoded",
        striped: true, 
        cache: false,    
        pagination: true,   
        sortable: false,    
        sortOrder: "asc",    
        pageSize: 20,    
        pageList: [15, 20, 25, 30, 50],
        url: "./query",
        queryParams: function(params){
            return parameters;
        },
        sidePagination: "client",
        search: false,   
        strictSearch: false,
        columns: cols
    };
}

function fill_data_pack(data){
    return {
        striped: true,
        data: data
    };
}

function fill_details(index, parameters){
    return {
        method: 'POST',
        contentType: "application/x-www-form-urlencoded",
        striped: true, 
        cache: false,    
        pagination: true,   
        sortable: false,    
        sortOrder: "asc",    
        pageSize: 20,    
        pageList: [15, 20, 25, 30, 50],
        url: "./query",
        queryParams: function(params){
            return parameters;
        },
        sidePagination: "client",
        search: false,   
        strictSearch: false,
        columns: [
            {
                field: "ADDR1",
                title: "地区"
            },
            {
                field: "ADDR2",
                title: "单位"
            },
            {
                field: "PORTNAME",
                title: "闸号"
            },
            {
                field: "SHIPID",
                title: "船队编号"
            },
            {
                field: "REGDATE",
                title: "登记时间"
            },
            {
                field: "SCHEDULEDATE",
                title: "调度时间"
            },
            {
                field: "SHIPCOUNT",
                title: "船只数目"
            },
            {
                field: "SHIPTYPE",
                title: "运送货物种类"
            },
            {
                field: "TOTALWEIGHT",
                title: "船只总载重"
            },
            {
                field: "DIRECTION",
                title: "上下行标志",
                formatter: function(value, row, index){
                    if(value === 'U ') return "上行";
                    return "下行";
                }
            },
            {
                field: "PASSTIME",
                title: "通过时间(h)"
            }
        ]
     };
}