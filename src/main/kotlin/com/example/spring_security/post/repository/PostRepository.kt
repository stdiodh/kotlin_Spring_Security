package com.example.spring_security.post.repository

import com.example.spring_security.post.entity.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PostRepository : JpaRepository<Post, Long>{
    /**
     * 스프링부트 프로젝트의 터미널에는 해당 메소드에 대한 SQL로그를 확인할 수 있는데
     * 기존에 데이터에서 하위 데이터 조회를 위해 추가적인 쿼리가 발생하고 있다.
     * 한번의 조회에 데이터의 갯수 N에 따라서 추가적인 쿼리가 발생하여
     * N+1 문제가 발생하기에 JOIN FETCH 이용를 이용한 것이다.
     * JOIN 쿼리의 경우 default값이 INNER JOIN을 실행하기 때문에 LEFT JOIN을 사용하여 댓글이 없어도 모두 JOIN하도록 지정하였다.
     */
    @Query(value = "SELECT p FROM Post p LEFT JOIN FETCH p.comments")
    fun findAllByFetchJoin() : List<Post>
}