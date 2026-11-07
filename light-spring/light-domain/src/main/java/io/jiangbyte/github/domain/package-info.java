/**
 * 领域模型所在层：按业务边界划分一级子包（例如 order、user）。
 *
 * 单个上下文内推荐目录：
 * - model/entity：可变业务实体
 * - model/valobj：不可变或描述性值对象
 * - model/aggregate：聚合根及相关一致性边界
 * - adapter/repository：持久化端口（接口）
 * - adapter/port：访问外部系统的端口（接口）
 * - service：跨实体的领域行为编排
 *
 * Admin 与 Portal 共用本层模型，不要按入口拆两套领域。
 * 开新业务时，复制 template 子树并改成真实上下文名。
 */
package io.jiangbyte.github.domain;
