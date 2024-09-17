//package ecab;
//
//import com.ecab.model.RideResult;
//import com.ecab.service.CacheService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@DataRedisTest
//public class RedisCacheIntegrationTest {
//
//    @Autowired
//    private RedisTemplate<String, RideResult> redisTemplate;
//
//    @Autowired
//    private CacheService cacheService;
//
//    private RideResult rideResult;
//
//    @BeforeEach
//    public void setUp() {
//        rideResult = new RideResult("passenger1", null, 40.730610, -73.935242);
//    }
//
//    @Test
//    public void testCacheRideResult() {
//        cacheService.cacheRideResult("ride1", rideResult);
//        RideResult cachedResult = redisTemplate.opsForValue().get("ride1");
//        assertEquals("passenger1", cachedResult.getPassengerId());
//    }
//}
