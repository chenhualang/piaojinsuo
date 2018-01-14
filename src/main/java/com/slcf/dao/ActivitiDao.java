package com.slcf.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.slcf.pojo.BillNote;
import com.slcf.pojo.UserBean;

@Repository
public interface ActivitiDao {

	/**
	 * 添加请假单
	 * @param note
	 * @return
	 */
	@Insert("insert into tb_bill (name,type,reason,startTime,endTime,reMark,status,user_id)"
			+ " values(#{name},#{type},#{reason},#{startTime},#{endTime},#{reMark},#{status},#{user_id})")
	public int saveBill(BillNote note);
	
	/**
	 * 提交请假单
	 * @param status
	 * @param bid
	 * @return
	 */
	@Update("update tb_bill set optTime=sysdate(),status=#{status} where bill_id=#{bid}")
	public int upBill(@Param("status")String status,@Param("bid")int bid);
	
	/**
	 * 修改状态
	 * @param bid
	 * @param status
	 * @return
	 */
	@Update("update tb_bill set status=#{status},optName=#{optName} where bill_id=#{bid}")
	public int upBillByTask(@Param("optName")String optName,@Param("bid")int bid,@Param("status")String status);
	/**
	 * 获取最新添加的信息
	 * @return
	 */
	@Select("select * from tb_bill where user_id=#{user_id} order by bill_id desc limit 0,1")
	public BillNote singleResult(@Param("user_id")int user_id);
	
	/**
	 * 跟据请假单id查询
	 * @param bid
	 * @return
	 */
	@Select("select * from tb_bill where bill_id=#{bid}")
	public BillNote queryBillById(@Param("bid")int bid);
	/**
	 * 根据用户id查询此用户的请假单
	 * @param user_id
	 * @param spage
	 * @param epage
	 * @return
	 */
	@Select("select * from tb_bill where user_id=#{user_id} order by bill_id desc limit #{spage},#{epage}")
	public List<BillNote> getMyReq(@Param("user_id")int user_id,@Param("spage")int spage,@Param("epage")int epage);
	
	/**
	 * 根据用户id统计数量
	 * @param id
	 * @return
	 */
	@Select("select count(*)from tb_bill where user_id=#{uid}")
	public int getCountByUserId(@Param("uid")int uid);
	
	/**
	 * 根据用户id查询当前用户所在的部门
	 * @param uid
	 * @return
	 */
	@Select("select dept_id from tb_user where user_id=#{uid}")
	public int queryDeptId(int uid);
	
	/**
	 * 查询部门经理
	 * @param deptId
	 * @return
	 */
	@Select("select DISTINCT u.* from tb_user u,tb_user_role ur where u.dept_id=#{deptId} and ur.role_id=2 and u.user_id =ur.user_id order by user_id desc limit 0,1")
	public UserBean queryUserSingleResult(int deptId);
	
	/**
	 * 根据请假单id删除
	 * @param bid
	 * @return
	 */
	@Delete("delete from tb_bill where bill_id=#{bid}")
	public int delBillById(int bid);
	
	/**
	 * 查询角色是总经理的用户
	 * @return
	 */
	@Select("select u.* from tb_user u,tb_user_role ur where  u.user_id=ur.user_id and ur.role_id=3 order by u.user_id desc limit 0,1")
	public UserBean getAdmin();
	
	/**
	 * 查询角色是人事部经理的用户
	 * @return
	 */
	@Select("select u.* from tb_user u,tb_user_role ur where u.dept_id=2 and  u.user_id=ur.user_id and ur.role_id=2 order by u.user_id desc  limit 0,1")
	public UserBean getPersonel();
}
