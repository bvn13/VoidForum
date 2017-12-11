package ru.bvn13.voidforum.forms;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * bvn13 <mail4bvn@gmail.com>
 */
@Data
public class SettingsForm {

    @NotEmpty
    @NotNull
    private String siteName;

    @NotNull
    private String siteSlogan;

    @NotNull
    private Integer pageSize;

    @NotNull
    private String storagePath;

    @NotNull
    private String mainUri;

    @NotNull
    private Integer commentsPageSize;

}
