package io.jiangbyte.github.trigger.admin.content.announcement.http;

import io.jiangbyte.github.domain.content.announcement.model.entity.AnnouncementEntity;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementPriority;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementStatus;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementType;
import io.jiangbyte.github.trigger.admin.content.announcement.application.AdminAnnouncementApplicationService;
import io.jiangbyte.github.trigger.admin.content.announcement.mapper.AdminAnnouncementMapper;
import io.jiangbyte.github.types.page.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
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
class AdminAnnouncementHttpContractTest {
    @Mock
    private AdminAnnouncementApplicationService applicationService;

    private final AdminAnnouncementMapper mapper = Mappers.getMapper(AdminAnnouncementMapper.class);

    @Test
    void pageUsesPageEndpointAndQueryParameters() throws Exception {
        when(applicationService.page(any()))
                .thenReturn(new PageResult<>(List.of(announcement()), 1, 1, 20));
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(get("/api/announcements/page")
                        .param("page", "1")
                        .param("pageSize", "20")
                        .param("type", "NOTICE")
                        .param("priority", "IMPORTANT")
                        .param("status", "DRAFT"))
                .andExpect(status().isOk());

        verify(applicationService).page(any());
    }

    @Test
    void listIsNotUsedForPagination() throws Exception {
        mockMvc().perform(get("/api/announcements/list")).andExpect(status().isNotFound());
    }

    @Test
    void createMapsPayloadToCommand() throws Exception {
        when(applicationService.create(any())).thenReturn(announcement());

        mockMvc().perform(post("/api/announcements/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title":"系统通知",
                                  "summary":"维护安排",
                                  "content":"本周六维护",
                                  "type":"NOTICE",
                                  "priority":"IMPORTANT"
                                }
                                """))
                .andExpect(status().isOk());

        verify(applicationService).create(any());
    }

    @Test
    void deleteAcceptsIdArray() throws Exception {
        mockMvc().perform(post("/api/announcements/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"ids":["1","2"]}
                                """))
                .andExpect(status().isOk());

        verify(applicationService).delete(List.of(1L, 2L));
    }

    @Test
    void publishAndArchiveUsePostActions() throws Exception {
        when(applicationService.publish(any())).thenReturn(announcement());
        when(applicationService.archive(11L)).thenReturn(announcement());
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(post("/api/announcements/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":"11","publish_at":"2026-12-01 10:00:00"}
                                """))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/announcements/archive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":"11"}
                                """))
                .andExpect(status().isOk());

        verify(applicationService).publish(any());
        verify(applicationService).archive(11L);
    }

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(
                new AdminAnnouncementController(applicationService, mapper)).build();
    }

    private AnnouncementEntity announcement() {
        LocalDateTime now = LocalDateTime.now();
        return new AnnouncementEntity(11L, "系统通知", "维护安排", "本周六维护",
                AnnouncementType.NOTICE, AnnouncementPriority.IMPORTANT, AnnouncementStatus.DRAFT,
                null, null, 7L, 7L, now, now);
    }
}
