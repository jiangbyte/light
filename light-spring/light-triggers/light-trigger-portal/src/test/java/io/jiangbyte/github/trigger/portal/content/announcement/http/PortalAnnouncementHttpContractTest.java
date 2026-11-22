package io.jiangbyte.github.trigger.portal.content.announcement.http;

import io.jiangbyte.github.domain.content.announcement.model.entity.AnnouncementEntity;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementPriority;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementStatus;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementType;
import io.jiangbyte.github.trigger.portal.content.announcement.application.PortalAnnouncementApplicationService;
import io.jiangbyte.github.trigger.portal.content.announcement.mapper.PortalAnnouncementMapper;
import io.jiangbyte.github.types.page.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PortalAnnouncementHttpContractTest {
    @Mock
    private PortalAnnouncementApplicationService applicationService;

    private final PortalAnnouncementMapper mapper = Mappers.getMapper(PortalAnnouncementMapper.class);

    @Test
    void pageUsesPublishedQuery() throws Exception {
        when(applicationService.publishedPage(any()))
                .thenReturn(new PageResult<>(List.of(announcement()), 1, 1, 20));

        mockMvc().perform(get("/api/announcements/page")
                        .param("page", "1")
                        .param("pageSize", "20")
                        .param("type", "NOTICE"))
                .andExpect(status().isOk());

        verify(applicationService).publishedPage(any());
    }

    @Test
    void detailUsesPublishedDetail() throws Exception {
        when(applicationService.publishedDetail(11L)).thenReturn(announcement());

        mockMvc().perform(get("/api/announcements/detail").param("id", "11"))
                .andExpect(status().isOk());

        verify(applicationService).publishedDetail(11L);
    }

    @Test
    void portalDoesNotExposeCreateAction() throws Exception {
        mockMvc().perform(post("/api/announcements/create")).andExpect(status().isNotFound());
    }

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(
                new PortalAnnouncementController(applicationService, mapper)).build();
    }

    private AnnouncementEntity announcement() {
        LocalDateTime now = LocalDateTime.now();
        return new AnnouncementEntity(11L, "系统通知", "维护安排", "本周六维护",
                AnnouncementType.NOTICE, AnnouncementPriority.IMPORTANT, AnnouncementStatus.PUBLISHED,
                now.minusHours(1), null, 7L, 7L, now.minusDays(1), now);
    }
}
