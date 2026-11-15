package me.weishu.kernelsu.data.repository

interface SettingsRepository {
    var uiMode: String
    var checkUpdate: Boolean
    var checkModuleUpdate: Boolean
    var themeMode: Int
    var miuixMonet: Boolean
    var keyColor: Int
    var colorStyle: String
    var colorSpec: String
    var enablePredictiveBack: Boolean
    var enableSwipeDismiss: Boolean
    var pagerInterceptionMode: Int
    var enableBlur: Boolean
    var enableFloatingBottomBar: Boolean
    var enableFloatingBottomBarBlur: Boolean
    var enableNavigationBadge: Boolean
    var navigationRailExpanded: Boolean
    var pageScale: Float
    var moduleDescriptionMaxLines: Int
    var enableWebDebugging: Boolean
    var moduleSortEnabledFirst: Boolean
    var moduleSortActionFirst: Boolean
    /** Name of the [me.weishu.kernelsu.ui.screen.modulerepo.RepoSort] entry. */
    var moduleRepoSortOrder: String
    var superuserShowSystemApps: Boolean
    var superuserShowOnlyPrimaryUserApps: Boolean
    var superuserSortOption: Int
    var suLogFilters: Set<String>?
    var autoJailbreak: Boolean
    var useSoftReboot: Boolean
    val intentToken: String

    /** 首页 Hero 卡立绘的绝对路径，空串表示未设置。 */
    var moeHeroPath: String

    /** 首页 Hero 卡立绘的排法：0 = 侧图，1 = 满铺。 */
    var moeHeroLayout: Int

    /** 全 App 背景图的绝对路径，空串表示未设置。 */
    var moeBgPath: String

    /** 卡片不透明度 0.30~1.0 */
    var moeCardAlpha: Float

    /** 背景模糊半径（dp） */
    var moeBgBlur: Float

    /** 底部导航栏不透明度 0.5~1.0 */
    var moeBarAlpha: Float

    /** 自定义主页台词，空串 = 用默认台词 */
    var moeTitle: String

    /** 自定义主页副标题，空串 = 用默认 */
    var moeSubtitle: String

    /** Hero 卡专属标题，空串 = 跟随顶部台词 */
    var moeHeroTitle: String

    suspend fun getSuCompatStatus(): String
    suspend fun getSuCompatPersistValue(): Long?
    fun isSuEnabled(): Boolean
    fun setSuEnabled(enabled: Boolean): Boolean
    fun setSuCompatModePref(mode: Int)
    fun getSuCompatModePref(): Int

    suspend fun getKernelUmountStatus(): String
    fun isKernelUmountEnabled(): Boolean
    fun setKernelUmountEnabled(enabled: Boolean): Boolean

    suspend fun getSelinuxHideStatus(): String
    fun isSelinuxHideEnabled(): Boolean
    fun setSelinuxHideEnabled(enabled: Boolean): Int

    suspend fun getSulogStatus(): String
    suspend fun getSulogPersistValue(): Long?
    suspend fun getSulogValue(): Long?
    fun setSulogEnabled(enabled: Boolean): Boolean

    suspend fun getAdbRootStatus(): String
    suspend fun getAdbRootPersistValue(): Long?
    suspend fun getAdbRootValue(): Long?
    fun setAdbRootEnabled(enabled: Boolean): Boolean
    suspend fun getShellSuAdbOnlyStatus(): String
    suspend fun getShellSuAdbOnlyPersistValue(): Long?
    suspend fun getShellSuAdbOnlyValue(): Long?
    fun setShellSuAdbOnlyEnabled(enabled: Boolean): Boolean

    fun isDefaultUmountModules(): Boolean
    fun setDefaultUmountModules(enabled: Boolean): Boolean

    fun isLkmMode(): Boolean

    fun execKsudFeatureSave()
}
